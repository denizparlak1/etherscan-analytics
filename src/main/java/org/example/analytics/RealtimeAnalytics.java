package org.example.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.example.model.AnalyticsTransaction;
import org.example.model.TopAddress;
import org.example.model.TopReceivingAddress;
import org.example.service.TopAddressService;
import org.example.service.TopReceivingAddressService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RealtimeAnalytics {

    public static void analyzeTransactions(DataStream<String> stream) {
        DataStream<AnalyticsTransaction> transactionStream = stream
                .map(new TransactionMapper());

        transactionStream
                .map(transaction -> new Tuple2<>(transaction.getFrom(), 1L))
                .returns(Types.TUPLE(Types.STRING, Types.LONG))
                .keyBy(tuple -> "global")
                .process(new FinalTopNProcessFunction(3));

        transactionStream
                .keyBy(transaction -> "global")
                .process(new TopReceivingAddressesFunction(3));
    }

    public static class TransactionMapper extends RichMapFunction<String, AnalyticsTransaction> {
        private transient ObjectMapper objectMapper;

        @Override
        public void open(Configuration parameters) throws Exception {
            objectMapper = new ObjectMapper();
        }

        @Override
        public AnalyticsTransaction map(String value) throws Exception {
            return objectMapper.readValue(value, AnalyticsTransaction.class);
        }
    }

    public static class FinalTopNProcessFunction extends KeyedProcessFunction<String, Tuple2<String, Long>, String> {
        private final int topSize;
        private transient MapState<String, Long> state;
        private transient List<Tuple2<String, Long>> lastTopN;
        private transient boolean timerRegistered;

        private transient TopAddressService topAddressService;

        public FinalTopNProcessFunction(int topSize) {
            this.topSize = topSize;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            MapStateDescriptor<String, Long> descriptor = new MapStateDescriptor<>(
                    "addressCounts",
                    Types.STRING,
                    Types.LONG
            );
            state = getRuntimeContext().getMapState(descriptor);
            lastTopN = new ArrayList<>();
            timerRegistered = false;

            topAddressService = new TopAddressService();
            topAddressService.initialize();
        }

        @Override
        public void processElement(Tuple2<String, Long> value, Context ctx, Collector<String> out) throws Exception {
            String address = value.f0;
            Long count = state.get(address);
            if (count == null) {
                count = value.f1;
            } else {
                count += value.f1;
            }
            state.put(address, count);

            if (!timerRegistered) {
                ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 1000);
                timerRegistered = true;
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            List<Tuple2<String, Long>> allCounts = new ArrayList<>();
            for (Map.Entry<String, Long> entry : state.entries()) {
                allCounts.add(new Tuple2<>(entry.getKey(), entry.getValue()));
            }

            if (allCounts.isEmpty()) {
                return;
            }

            allCounts.sort(Comparator.comparingLong(o -> -o.f1));
            List<Tuple2<String, Long>> topN = allCounts.subList(0, Math.min(topSize, allCounts.size()));

            if (!topN.equals(lastTopN)) {
                updateDatabase(topN);

                lastTopN = new ArrayList<>(topN);
            }

            ctx.timerService().registerProcessingTimeTimer(timestamp + 1000);
        }

        private void updateDatabase(List<Tuple2<String, Long>> topN) {
            List<String> newAddresses = new ArrayList<>();
            int rank = 1;

            for (Tuple2<String, Long> entry : topN) {
                TopAddress topAddress = new TopAddress();
                topAddress.setRank(rank);
                topAddress.setAddress(entry.f0);
                topAddress.setCount(entry.f1);

                topAddressService.saveOrUpdate(topAddress);

                newAddresses.add(entry.f0);
                rank++;
            }

            List<TopAddress> existingTopAddresses = topAddressService.getAllTopAddresses();

            for (TopAddress existingAddress : existingTopAddresses) {
                if (!newAddresses.contains(existingAddress.getAddress())) {
                    topAddressService.deleteTopAddress(existingAddress);
                }
            }
        }
    }

    public static class TopReceivingAddressesFunction extends KeyedProcessFunction<String, AnalyticsTransaction, String> {
        private final int topSize;
        private transient MapState<String, Long> state;
        private transient List<Tuple2<String, Long>> lastTopN;
        private transient boolean timerRegistered;

        private transient TopReceivingAddressService topReceivingAddressService;

        public TopReceivingAddressesFunction(int topSize) {
            this.topSize = topSize;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            MapStateDescriptor<String, Long> descriptor = new MapStateDescriptor<>(
                    "addressValues",
                    Types.STRING,
                    Types.LONG
            );
            state = getRuntimeContext().getMapState(descriptor);
            lastTopN = new ArrayList<>();
            timerRegistered = false;

            topReceivingAddressService = new TopReceivingAddressService();
            topReceivingAddressService.initialize();
        }

        @Override
        public void processElement(AnalyticsTransaction value, Context ctx, Collector<String> out) throws Exception {
            String address = value.getTo();
            Long totalValue = state.get(address);
            if (totalValue == null) {
                totalValue = value.getValue();
            } else {
                totalValue += value.getValue();
            }
            state.put(address, totalValue);

            if (!timerRegistered) {
                ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 1000);
                timerRegistered = true;
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            List<Tuple2<String, Long>> allValues = new ArrayList<>();
            for (Map.Entry<String, Long> entry : state.entries()) {
                allValues.add(new Tuple2<>(entry.getKey(), entry.getValue()));
            }

            if (allValues.isEmpty()) {
                return;
            }

            allValues.sort(Comparator.comparingLong(o -> -o.f1));
            List<Tuple2<String, Long>> topN = allValues.subList(0, Math.min(topSize, allValues.size()));

            if (!topN.equals(lastTopN)) {
                updateDatabase(topN);

                lastTopN = new ArrayList<>(topN);
            }

            ctx.timerService().registerProcessingTimeTimer(timestamp + 1000);
        }

        private void updateDatabase(List<Tuple2<String, Long>> topN) {
            List<String> newAddresses = new ArrayList<>();
            int rank = 1;

            for (Tuple2<String, Long> entry : topN) {
                TopReceivingAddress topReceivingAddress = new TopReceivingAddress();
                topReceivingAddress.setRank(rank);
                topReceivingAddress.setAddress(entry.f0);
                topReceivingAddress.setTotalValue(entry.f1);

                topReceivingAddressService.saveOrUpdate(topReceivingAddress);

                newAddresses.add(entry.f0);
                rank++;
            }

            List<TopReceivingAddress> existingTopReceivingAddresses = topReceivingAddressService.getAllTopReceivingAddresses();

            for (TopReceivingAddress existingAddress : existingTopReceivingAddresses) {
                if (!newAddresses.contains(existingAddress.getAddress())) {
                    topReceivingAddressService.deleteTopReceivingAddress(existingAddress);
                }
            }
        }
    }
}

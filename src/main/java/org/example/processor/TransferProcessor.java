package org.example.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.example.config.KafkaConfig;
import org.example.model.Transaction;
import org.example.service.TransactionService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;

public class TransferProcessor {

    public static void processAndSendTransfers(DataStream<String> stream, KafkaConfig kafkaConfig) {
        final TransactionService transactionService = new TransactionService();

        DataStream<String> processedTransfers = stream
                .filter(new TransferFilter())
                .map(new TransferMapper(transactionService))
                .filter(details -> details != null);

        String outputTopic = kafkaConfig.getOutputTopic();

        FlinkKafkaProducer<String> kafkaProducer = new FlinkKafkaProducer<>(
                outputTopic,
                new SimpleStringSchema(),
                kafkaConfig.getProperties()
        );

        processedTransfers.addSink(kafkaProducer);
    }

    public static class TransferFilter implements FilterFunction<String> {
        private transient ObjectMapper objectMapper;

        @Override
        public boolean filter(String value) throws Exception {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }

            try {
                JsonNode node = objectMapper.readTree(value);

                if (node.has("functionName")) {
                    String functionName = node.get("functionName").asText();

                    return functionName.contains("transfer");
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static class TransferMapper extends RichMapFunction<String, String> {
        private transient ObjectMapper objectMapper;
        private final TransactionService transactionService;

        public TransferMapper(TransactionService transactionService) {
            this.transactionService = transactionService;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            objectMapper = new ObjectMapper();
            transactionService.initialize();
        }

        @Override
        public String map(String json) throws Exception {
            return extractTransferDetailsAndSave(json);
        }

        private String extractTransferDetailsAndSave(String json) throws Exception {
            JsonNode node = objectMapper.readTree(json);

            String from = node.get("from").asText();
            String input = node.get("input").asText();
            String timestamp = node.get("timeStamp").asText();
            String hash = node.get("hash").asText();
            String functionName = node.get("functionName").asText();

            String toAddress = "";
            BigInteger value = BigInteger.ZERO;

            String inputData = input.substring(10);

            if (functionName.contains("transferFrom")) {
                String fromAddressHex = inputData.substring(0, 64);
                String toAddressHex = inputData.substring(64, 128);
                String valueHex = inputData.substring(128, 192);

                toAddress = "0x" + toAddressHex.substring(24);
                value = new BigInteger(valueHex, 16);
            } else if (functionName.contains("transfer")) {
                String toAddressHex = inputData.substring(0, 64);
                String valueHex = inputData.substring(64, 128);

                toAddress = "0x" + toAddressHex.substring(24);
                value = new BigInteger(valueHex, 16);
            } else {
                return null;
            }

            int decimals = 6;
            BigDecimal tokenValue = new BigDecimal(value).divide(new BigDecimal(BigInteger.TEN.pow(decimals)));

            long timeInSeconds = Long.parseLong(timestamp);
            String readableDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date(timeInSeconds * 1000));

            Transaction transaction = new Transaction();
            transaction.setTransactionId(hash);
            transaction.setFromAddress(from);
            transaction.setToAddress(toAddress);
            transaction.setValue(tokenValue.doubleValue());
            transaction.setTransactionDate(readableDate);

            transactionService.saveTransaction(transaction);

            ObjectMapper jsonMapper = new ObjectMapper();
            return jsonMapper.createObjectNode()
                    .put("transactionId", hash)
                    .put("from", from)
                    .put("to", toAddress)
                    .put("value", tokenValue.doubleValue())
                    .put("date", readableDate)
                    .toString();
        }
    }
}

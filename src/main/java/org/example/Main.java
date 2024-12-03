package org.example;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.example.config.KafkaConfig;
import org.example.kafka.KafkaTransferConsumer;
import org.example.processor.TransferProcessor;
import org.example.analytics.RealtimeAnalytics;

public class Main {
    public static void main(String[] args) throws Exception {
        KafkaConfig kafkaConfig = new KafkaConfig();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inputKafkaStream = KafkaTransferConsumer.createInputConsumer(env, kafkaConfig);
        TransferProcessor.processAndSendTransfers(inputKafkaStream, kafkaConfig);

        DataStream<String> outputKafkaStream = KafkaTransferConsumer.createOutputConsumer(env, kafkaConfig);
        RealtimeAnalytics.analyzeTransactions(outputKafkaStream);

        env.execute("Ethereum Transfer Processor and Realtime Analytics");
    }
}

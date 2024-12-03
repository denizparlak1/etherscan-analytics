package org.example.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.example.config.KafkaConfig;

import java.util.Properties;

public class KafkaTransferConsumer {

    public static DataStream<String> createConsumer(StreamExecutionEnvironment env, String topic, KafkaConfig kafkaConfig) {
        Properties properties = kafkaConfig.getProperties();

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                topic,
                new SimpleStringSchema(),
                properties
        );

        consumer.setStartFromEarliest();

        return env.addSource(consumer);
    }

    public static DataStream<String> createInputConsumer(StreamExecutionEnvironment env, KafkaConfig kafkaConfig) {
        return createConsumer(env, kafkaConfig.getInputTopic(), kafkaConfig);
    }

    public static DataStream<String> createOutputConsumer(StreamExecutionEnvironment env, KafkaConfig kafkaConfig) {
        return createConsumer(env, kafkaConfig.getOutputTopic(), kafkaConfig);
    }
}

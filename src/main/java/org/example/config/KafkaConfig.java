package org.example.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaConfig {

    private Properties properties;

    public KafkaConfig() {
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = KafkaConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties file not found.");
            }
            this.properties.load(input);

            properties.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create Kafka connection.", e);
        }
    }

    public Properties getProperties() {
        if (!properties.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)) {
            throw new RuntimeException("The address of Kafka brokers must be specified in the 'bootstrap.servers' parameter.");
        }
        return this.properties;
    }

    public String getInputTopic() {
        return properties.getProperty("kafka.input.topic", "incoming-transactions");
    }

    public String getOutputTopic() {
        return properties.getProperty("kafka.output.topic", "parsed-transactions");
    }
}

package ru.stepagin.core.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.stepagin.core.entity.ImageEntity;
import ru.stepagin.core.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {

    @Value(value = "${spring.kafka.bootstrap-server}")
    private String kafkaServer;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean
    public Map<String, Object> imageProducerConfigs() {
        Map<String, Object> props = producerConfigs();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> userProducerConfigs() {
        Map<String, Object> props = producerConfigs();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, ImageEntity> imageProducerFactory() {
        return new DefaultKafkaProducerFactory<>(imageProducerConfigs());
    }

    @Bean
    public ProducerFactory<String, UserEntity> userProducerFactory() {
        return new DefaultKafkaProducerFactory<>(userProducerConfigs());
    }

    @Bean(name = "image-kafka-template")
    public KafkaTemplate<String, ImageEntity> imageKafkaTemplate() {
        return new KafkaTemplate<>(imageProducerFactory());
    }

    @Bean(name = "user-kafka-template")
    public KafkaTemplate<String, UserEntity> userKafkaTemplate() {
        return new KafkaTemplate<>(userProducerFactory());
    }
}

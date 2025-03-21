package com.solicitudes.ingr.config;

import com.solicitudes.model.Solicitud;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de Kafka para el servicio INGR.
 * Define los beans necesarios para producir y consumir mensajes de Kafka.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Configura la fábrica de consumidores para Kafka.
     * Permite deserializar mensajes JSON al objeto Solicitud.
     * 
     * @return ConsumerFactory configurado para consumir solicitudes
     */
    @Bean
    public ConsumerFactory<String, Solicitud> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ingr-group");
        
        JsonDeserializer<Solicitud> deserializer = new JsonDeserializer<>(Solicitud.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Configura el contenedor para los listeners de Kafka.
     * 
     * @return Factory configurada para los listeners
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Solicitud> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Solicitud> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    /**
     * Configura la fábrica de productores para Kafka.
     * Permite serializar objetos Solicitud a formato JSON.
     * 
     * @return ProducerFactory configurado para producir solicitudes
     */
    @Bean
    public ProducerFactory<String, Solicitud> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Crea un KafkaTemplate para enviar mensajes.
     * 
     * @return KafkaTemplate configurado para enviar solicitudes
     */
    @Bean
    public KafkaTemplate<String, Solicitud> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
} 
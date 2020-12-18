package com.example.motioner.infrastructure.configuration;

import com.example.motioner.infrastructure.MqttMessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.UUID;

@Configuration
public class MqttConfiguration {

    @Value("${mqtt.broker.host}")
    private String brokerHost;

    @Value("${mqtt.broker.port}")
    private String brokerPort;

    @Value("${mqtt.broker.topics}")
    private String topics;

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        String[] parsedTopics = parseTopics();
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        "tcp://" + brokerHost + ":" + brokerPort,
                        UUID.randomUUID().toString(),
                        parsedTopics);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    private String[] parseTopics() {
        return topics.split(",");
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MqttMessageHandler();
    }
}

package com.example.motioner.infrastructure;

import com.example.motioner.domain.entity.Alarm;
import com.example.motioner.domain.entity.Device;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.Message;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MqttMessageHandler implements MessageHandler {

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    private Gson gson = new GsonBuilder().create();

    private DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:m:s");

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String payload = (String) message.getPayload();
        Map<String, String> parsedMessage = (Map<String, String>) gson.fromJson(payload, Map.class);
        long occurredAt = 0L;
        try {
            occurredAt = sdf.parse(parsedMessage.get("occurred_at")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        UUID deviceID = UUID.fromString(parsedMessage.get("device_id"));
        Device device = new Device(deviceID, "", new Date().getTime(), occurredAt);
        deviceRepository.saveAndFlush(device);

        Alarm alarm = new Alarm(
                UUID.fromString(parsedMessage.get("id")),
                parsedMessage.get("place"),
                parsedMessage.get("filename"),
                parsedMessage.get("type"),
                device,
                occurredAt,
                false
        );
        alarmRepository.saveAndFlush(alarm);
    }
}

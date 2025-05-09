package com.sincera.tcp.client.controller;

import com.sincera.tcp.client.service.JsonMessageSenderService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/tcp/client")
public class TCPClientControllerOptimized {

    private static final Logger log = LoggerFactory.getLogger(TCPClientControllerOptimized.class);

    @Autowired
    private JsonMessageSenderService service;

    @Value("${uap.attr.names}")
    private String UAP_ATTRS;

    private List<String> keys;

    @PostConstruct
    private void init() {
        keys = Arrays.asList(UAP_ATTRS.split(","));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postTcpMessages(@RequestBody String payload) {
        log.info("Received payload: {}", payload);
        processPayloadAsync(payload);
        return ResponseEntity.ok("success");
    }

    @Async
    public void processPayloadAsync(String payload) {
        try {
            JSONArray jsonArray = new JSONArray(payload);
            JSONObject originalObject = jsonArray.getJSONObject(0);
            JSONObject newJsonObject = new JSONObject();

            for (String key : keys) {
                try {
                    if (originalObject.has(key) && !originalObject.isNull(key)) {
                        newJsonObject.put(key.trim(), originalObject.getString(key));
                    } else {
                        newJsonObject.put(key, JSONObject.NULL);
                    }
                } catch (Exception e) {
                    log.warn("Error processing key {}: {}", key, e.getMessage());
                    newJsonObject.put(key, JSONObject.NULL);
                }
            }

            log.info("Processed message: {}", newJsonObject);
            service.startSendingMessages(newJsonObject.toString());
            log.info("Message sent successfully.");
        } catch (Exception e) {
            log.error("Failed to process payload: {}", e.getMessage());
        }
    }
}

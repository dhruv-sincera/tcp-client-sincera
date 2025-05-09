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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/tcp/client")
public class TCPClientController {

    /*private static final Logger log = LoggerFactory.getLogger(TCPClientController.class);

    @Autowired
    private JsonMessageSenderService service;

    @Value("${uap.attr.names}")
    private String UAP_ATTRS;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postTcpMessages(@RequestBody String payload){
        log.info("Payload to the TCP Client connector - \n"+payload);


        // Parse the JSON array
        JSONArray jsonArray = new JSONArray(payload);
        JSONObject originalObject = jsonArray.getJSONObject(0);

        // Create a new JSON object with the desired fields
        JSONObject newJsonObject = new JSONObject();

        String[] keys = UAP_ATTRS.split(",");

        for (String key : keys) {
            try {
                if (originalObject.has(key) && !originalObject.isNull(key)) {
                    newJsonObject.put(key.trim(), originalObject.getString(key));
                } else {
                    newJsonObject.put(key, JSONObject.NULL);
                }
            } catch (Exception e) {
                // Optional: log or handle the exception
                System.out.println("Error retrieving key: " + key + " - " + e.getMessage());
                newJsonObject.put(key, JSONObject.NULL);
            }
        }

        log.info("Payload to send: "+newJsonObject.toString());
        service.startSendingMessages(newJsonObject.toString());
        log.info("Message sent.");
        return ResponseEntity.ok()
                .body("success");
    }*/
}

package com.transactionmonitoring.backend.service;

import com.transactionmonitoring.backend.entity.Alert;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class AlertStreamService {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data(Map.of("status", "connected")));
        } catch (IOException ex) {
            emitter.completeWithError(ex);
        }

        return emitter;
    }

    public void publishAlertCreated(Alert alert) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("alertId", alert.getAlertId());
        payload.put("transactionId", alert.getTransactionId());
        payload.put("ruleId", alert.getRuleId());
        payload.put("alertType", alert.getAlertType());
        payload.put("severity", alert.getSeverity());
        payload.put("alertStatus", alert.getAlertStatus());
        payload.put("alertMessage", alert.getAlertMessage());
        payload.put("createdAt", alert.getCreatedAt() != null ? alert.getCreatedAt().toString() : null);

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("alert-created").data(payload));
            } catch (IOException ex) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}
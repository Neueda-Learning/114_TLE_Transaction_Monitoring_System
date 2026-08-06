package com.transactionmonitoring.backend.controller;

import com.transactionmonitoring.backend.security.JwtUtil;
import com.transactionmonitoring.backend.service.AlertStreamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/alerts")
public class AlertStreamController {

    private final AlertStreamService alertStreamService;
    private final JwtUtil jwtUtil;

    public AlertStreamController(AlertStreamService alertStreamService, JwtUtil jwtUtil) {
        this.alertStreamService = alertStreamService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAlerts(@RequestParam String token) {
        if (!jwtUtil.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        return alertStreamService.subscribe();
    }
}
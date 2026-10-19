/**
 * @Author Łukasz Piętka (FUT 2025)
 */
package me.havooc.poligon.controller;

import lombok.RequiredArgsConstructor;
import me.havooc.poligon.model.SmsRequest;
import me.havooc.poligon.model.SmsResponse;
import me.havooc.poligon.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/send-sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping
    public ResponseEntity<?> send(@RequestBody SmsRequest smsRequest) {
        SmsResponse smsResponse;
        try {
            smsResponse = smsService.sendSms(smsRequest);
            return ResponseEntity.ok(smsResponse);
        } catch (Exception e) {
            smsResponse = new SmsResponse();
            smsResponse.setError(500);
            smsResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(smsResponse);
        }
    }
} 
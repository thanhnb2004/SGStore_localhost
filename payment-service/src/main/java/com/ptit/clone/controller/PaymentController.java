package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.CreatePaymentRequest;
import com.ptit.clone.dtos.request.MomoCallbackRequest;
import com.ptit.clone.dtos.response.PaymentResponse;
import com.ptit.clone.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/")
    public ResponseEntity<PaymentResponse> initPayment(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initPayment(userId, request));
    }

    @GetMapping("/")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUser(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.getPaymentsByUser(userId));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.getPayment(paymentId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.getPaymentByOrderId(orderId));
    }

    // Webhook callback từ MoMo sau khi thanh toán
    @PostMapping("/momo/callback")
    public ResponseEntity<Void> momoCallback(
            @RequestBody MomoCallbackRequest request
    ) {
        paymentService.handleMomoCallback(request);
        return ResponseEntity.ok().build();
    }

    // Xác nhận đã nhận hàng và thanh toán COD
    @PatchMapping("/{paymentId}/confirm-cod")
    public ResponseEntity<PaymentResponse> confirmCodPayment(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.confirmCodPayment(paymentId));
    }

    @PatchMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable UUID paymentId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.cancelPayment(paymentId, userId));
    }
}

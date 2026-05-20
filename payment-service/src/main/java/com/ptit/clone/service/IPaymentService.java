package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreatePaymentRequest;
import com.ptit.clone.dtos.request.MomoCallbackRequest;
import com.ptit.clone.dtos.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface IPaymentService {
    PaymentResponse initPayment(String userId, CreatePaymentRequest request);
    PaymentResponse getPayment(UUID paymentId);
    PaymentResponse getPaymentByOrderId(UUID orderId);
    List<PaymentResponse> getPaymentsByUser(String userId);
    void handleMomoCallback(MomoCallbackRequest request);
    PaymentResponse confirmCodPayment(UUID paymentId);
    PaymentResponse cancelPayment(UUID paymentId, String userId);
}

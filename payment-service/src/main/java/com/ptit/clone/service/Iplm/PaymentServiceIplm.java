package com.ptit.clone.service.Iplm;

import com.ptit.clone.config.properties.MomoProperties;
import com.ptit.clone.dtos.request.CreatePaymentRequest;
import com.ptit.clone.dtos.request.MomoCallbackRequest;
import com.ptit.clone.dtos.response.PaymentResponse;
import com.ptit.clone.entity.Payment;
import com.ptit.clone.messaging.event.PaymentCompletedEvent;
import com.ptit.clone.messaging.producer.PaymentCompletedProducer;
import com.ptit.clone.model.PaymentMethod;
import com.ptit.clone.model.PaymentStatus;
import com.ptit.clone.respository.IPaymentRepository;
import com.ptit.clone.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceIplm implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final PaymentCompletedProducer paymentCompletedProducer;
    private final MomoProperties momoProperties;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public PaymentResponse initPayment(String userId, CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(userId);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.PENDING);

        if (request.getMethod() == PaymentMethod.MOMO) {
            String payUrl = callMomoApi(payment);
            payment.setMomoPayUrl(payUrl);
        }

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUser(String userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void handleMomoCallback(MomoCallbackRequest request) {
        String expectedSignature = buildMomoCallbackSignature(request);
        if (!expectedSignature.equals(request.getSignature())) {
            log.warn("Invalid MoMo callback signature for orderId: {}", request.getOrderId());
            throw new RuntimeException("Invalid MoMo callback signature");
        }

        UUID orderId = UUID.fromString(request.getOrderId());
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + request.getOrderId()));

        if (request.getResultCode() == 0) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setMomoTransactionId(String.valueOf(request.getTransId()));
            paymentRepository.save(payment);
            paymentCompletedProducer.fire(new PaymentCompletedEvent(payment.getOrderId(), payment.getUserId(), payment.getAmount(), payment.getMethod()));
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("MoMo payment failed for orderId: {}, resultCode: {}, message: {}", request.getOrderId(), request.getResultCode(), request.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse confirmCodPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        if (payment.getMethod() != PaymentMethod.COD) {
            throw new RuntimeException("Payment method is not COD: " + paymentId);
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in PENDING status: " + paymentId);
        }

        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);
        paymentCompletedProducer.fire(new PaymentCompletedEvent(payment.getOrderId(), payment.getUserId(), payment.getAmount(), payment.getMethod()));
        return toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(UUID paymentId, String userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("Payment does not belong to user: " + userId);
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only PENDING payments can be cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
        return toResponse(payment);
    }

    private String callMomoApi(Payment payment) {
        String requestId = UUID.randomUUID().toString();
        String orderId = payment.getOrderId().toString();
        String orderInfo = "Thanh toan don hang " + orderId;
        String extraData = "";
        String requestType = "payWithMethod";

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + payment.getAmount()
                + "&extraData=" + extraData
                + "&ipnUrl=" + momoProperties.getIpnUrl()
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoProperties.getPartnerCode()
                + "&redirectUrl=" + momoProperties.getRedirectUrl()
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = hmacSha256(rawSignature, momoProperties.getSecretKey());

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", momoProperties.getPartnerCode());
        body.put("requestType", requestType);
        body.put("ipnUrl", momoProperties.getIpnUrl());
        body.put("redirectUrl", momoProperties.getRedirectUrl());
        body.put("orderId", orderId);
        body.put("amount", payment.getAmount());
        body.put("lang", "vi");
        body.put("orderInfo", orderInfo);
        body.put("requestId", requestId);
        body.put("extraData", extraData);
        body.put("signature", signature);

        ResponseEntity<Map> response = restTemplate.postForEntity(momoProperties.getApiEndpoint(), body, Map.class);

        if (response.getBody() == null || response.getBody().get("payUrl") == null) {
            throw new RuntimeException("Failed to get MoMo payment URL");
        }

        return (String) response.getBody().get("payUrl");
    }

    private String buildMomoCallbackSignature(MomoCallbackRequest request) {
        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + request.getAmount()
                + "&extraData=" + request.getExtraData()
                + "&message=" + request.getMessage()
                + "&orderId=" + request.getOrderId()
                + "&orderInfo=" + request.getOrderInfo()
                + "&orderType=" + request.getOrderType()
                + "&partnerCode=" + request.getPartnerCode()
                + "&payType=" + request.getPayType()
                + "&requestId=" + request.getRequestId()
                + "&responseTime=" + request.getResponseTime()
                + "&resultCode=" + request.getResultCode()
                + "&transId=" + request.getTransId();

        return hmacSha256(rawSignature, momoProperties.getSecretKey());
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .momoPayUrl(payment.getMomoPayUrl())
                .momoTransactionId(payment.getMomoTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

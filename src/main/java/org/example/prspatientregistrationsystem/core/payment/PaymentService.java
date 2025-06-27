package org.example.prspatientregistrationsystem.core.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${payu.merchant.id:145227}")
    private String merchantId;

    @Value("${payu.pos.id:145227}")
    private String posId;

    @Value("${payu.client.id:300746}")
    private String clientId;

    @Value("${payu.client.secret:2ee86a66e5d97e3fadc400c9f19b065d}")
    private String clientSecret;

    @Value("${payu.api.url:https://secure.snd.payu.com}")
    private String payuApiUrl;

    @Value("${payu.notify.url:http://localhost:8080/api/payment/callback}")
    private String notifyUrl;

    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequest) {
        try {
            log.info("Creating payment for visit: {}", paymentRequest.getVisitId());
            
            // Generate unique payment ID
            String paymentId = UUID.randomUUID().toString();
            
            // Create payment entity
            PaymentEntity paymentEntity = PaymentEntity.builder()
                    .paymentId(paymentId)
                    .visitId(paymentRequest.getVisitId())
                    .patientName(paymentRequest.getPatientName())
                    .patientEmail(paymentRequest.getPatientEmail())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .status("PENDING")
                    .description(paymentRequest.getDescription())
                    .build();
            
            paymentRepository.save(paymentEntity);
            log.info("Payment entity saved with ID: {}", paymentEntity.getId());
            
            // Get access token
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("Failed to get PayU access token");
                throw new RuntimeException("Failed to authenticate with PayU");
            }
            log.info("PayU access token obtained successfully");
            
            // Prepare PayU request
            Map<String, Object> payuRequest = createPayURequest(paymentRequest, paymentId);
            log.info("PayU request prepared: {}", payuRequest);
            
            // Send request to PayU
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payuRequest, headers);
            
            log.info("Sending request to PayU: {}", payuApiUrl + "/api/v2_1/orders");
            ResponseEntity<Map> response = restTemplate.exchange(
                    payuApiUrl + "/api/v2_1/orders",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            log.info("PayU response status: {}", response.getStatusCode());
            log.info("PayU response body: {}", response.getBody());
            
            if ((response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.FOUND) && response.getBody() != null) {
                Map<String, Object> payuResponse = response.getBody();
                String redirectUrl = (String) payuResponse.get("redirectUri");
                
                // Update payment with PayU order ID
                if (payuResponse.containsKey("orderId")) {
                    paymentEntity.setPaymentId(payuResponse.get("orderId").toString());
                    paymentRepository.save(paymentEntity);
                    log.info("Payment updated with PayU order ID: {}", paymentEntity.getPaymentId());
                }
                
                return PaymentResponseDto.builder()
                        .paymentId(paymentEntity.getPaymentId())
                        .redirectUrl(redirectUrl)
                        .status("SUCCESS")
                        .message("Payment created successfully")
                        .build();
            } else {
                log.error("PayU API returned error: status={}, body={}", response.getStatusCode(), response.getBody());
                log.error("PayU error details - Status Code: {}, Response Body: {}", response.getStatusCode(), response.getBody());
                if (response.getBody() != null) {
                    log.error("PayU error response as string: {}", response.getBody().toString());
                }
                throw new RuntimeException("Failed to create payment in PayU: " + response.getStatusCode() + " - " + response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Error creating payment", e);
            return PaymentResponseDto.builder()
                    .status("ERROR")
                    .message("Failed to create payment: " + e.getMessage())
                    .build();
        }
    }
    
    private Map<String, Object> createPayURequest(PaymentRequestDto paymentRequest, String paymentId) {
        Map<String, Object> request = new HashMap<>();
        
        log.info("Creating PayU request with paymentRequest: currency={}, amount={}, description={}", 
                paymentRequest.getCurrency(), paymentRequest.getAmount(), paymentRequest.getDescription());
        
        // Basic order information
        request.put("notifyUrl", notifyUrl);
        request.put("customerIp", "127.0.0.1");
        request.put("merchantPosId", posId);
        request.put("description", paymentRequest.getDescription());
        request.put("currencyCode", paymentRequest.getCurrency());
        
        // PayU expects amount in cents (multiply by 100)
        int amountInCents = paymentRequest.getAmount().multiply(new BigDecimal("100")).intValue();
        request.put("totalAmount", amountInCents);
        
        // Ext order ID
        request.put("extOrderId", paymentId);
        
        // Buyer information
        Map<String, Object> buyer = new HashMap<>();
        buyer.put("email", paymentRequest.getPatientEmail());
        
        // Split patient name into first and last name
        String[] nameParts = paymentRequest.getPatientName().split(" ", 2);
        buyer.put("firstName", nameParts[0]);
        buyer.put("lastName", nameParts.length > 1 ? nameParts[1] : "");
        buyer.put("language", "pl");
        request.put("buyer", buyer);
        
        // Products
        Map<String, Object> product = new HashMap<>();
        product.put("name", paymentRequest.getDescription());
        product.put("unitPrice", amountInCents);
        product.put("quantity", 1);
        request.put("products", new Object[]{product});
        
        log.info("Created PayU request with totalAmount: {} cents ({} PLN), currency: {}, merchantPosId: {}, extOrderId: {}", 
                amountInCents, paymentRequest.getAmount(), request.get("currencyCode"), request.get("merchantPosId"), request.get("extOrderId"));
        log.info("Buyer info: firstName={}, lastName={}, email={}", 
                buyer.get("firstName"), buyer.get("lastName"), buyer.get("email"));
        log.info("Full PayU request: {}", request);
        
        return request;
    }
    
    public String getAccessToken() {
        try {
            log.info("Getting PayU access token with client_id: {}", clientId);
            
            // Try different OAuth endpoints for PayU sandbox
            String tokenUrl = payuApiUrl + "/pl/standard/user/oauth/authorize";
            log.info("Token URL: {}", tokenUrl);
            
            // Create form data manually
            String formData = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s", 
                    clientId, clientSecret);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<String> entity = new HttpEntity<>(formData, headers);
            
            log.info("Sending token request with form data: grant_type=client_credentials&client_id={}&client_secret=***", clientId);
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            log.info("Token response status: {}", response.getStatusCode());
            log.info("Token response body: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String accessToken = (String) response.getBody().get("access_token");
                if (accessToken != null) {
                    log.info("Access token obtained successfully");
                    return accessToken;
                } else {
                    log.error("Access token not found in response");
                }
            } else {
                log.error("Failed to get access token: status={}, body={}", response.getStatusCode(), response.getBody());
                
                // Try alternative endpoint if first one fails
                String altTokenUrl = payuApiUrl + "/oauth/token";
                log.info("Trying alternative token URL: {}", altTokenUrl);
                
                ResponseEntity<Map> altResponse = restTemplate.exchange(
                        altTokenUrl,
                        HttpMethod.POST,
                        entity,
                        Map.class
                );
                
                log.info("Alternative token response status: {}", altResponse.getStatusCode());
                log.info("Alternative token response body: {}", altResponse.getBody());
                
                if (altResponse.getStatusCode() == HttpStatus.OK && altResponse.getBody() != null) {
                    String altAccessToken = (String) altResponse.getBody().get("access_token");
                    if (altAccessToken != null) {
                        log.info("Access token obtained successfully from alternative endpoint");
                        return altAccessToken;
                    }
                }
                
                // Try third alternative endpoint
                String thirdTokenUrl = payuApiUrl + "/pl/standard/user/oauth/token";
                log.info("Trying third alternative token URL: {}", thirdTokenUrl);
                
                ResponseEntity<Map> thirdResponse = restTemplate.exchange(
                        thirdTokenUrl,
                        HttpMethod.POST,
                        entity,
                        Map.class
                );
                
                log.info("Third alternative token response status: {}", thirdResponse.getStatusCode());
                log.info("Third alternative token response body: {}", thirdResponse.getBody());
                
                if (thirdResponse.getStatusCode() == HttpStatus.OK && thirdResponse.getBody() != null) {
                    String thirdAccessToken = (String) thirdResponse.getBody().get("access_token");
                    if (thirdAccessToken != null) {
                        log.info("Access token obtained successfully from third alternative endpoint");
                        return thirdAccessToken;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting access token", e);
        }
        
        return null;
    }
    
    public void handlePaymentCallback(Map<String, Object> callbackData) {
        try {
            String orderId = (String) callbackData.get("orderId");
            String status = (String) callbackData.get("status");
            
            PaymentEntity payment = paymentRepository.findByPaymentId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            
            payment.setStatus(status);
            paymentRepository.save(payment);
            
            log.info("Payment callback processed: orderId={}, status={}", orderId, status);
            
        } catch (Exception e) {
            log.error("Error processing payment callback", e);
            throw e;
        }
    }
    
    public Map<String, Object> getPaymentStatus(String paymentId) {
        try {
            PaymentEntity payment = paymentRepository.findByPaymentId(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            
            Map<String, Object> status = new HashMap<>();
            status.put("paymentId", payment.getPaymentId());
            status.put("status", payment.getStatus());
            status.put("amount", payment.getAmount());
            status.put("createdAt", payment.getCreatedAt());
            status.put("updatedAt", payment.getUpdatedAt());
            
            return status;
            
        } catch (Exception e) {
            log.error("Error getting payment status", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
} 
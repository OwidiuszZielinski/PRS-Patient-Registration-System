package org.example.prspatientregistrationsystem.core.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.example.prspatientregistrationsystem.core.visit.VisitDto;
import org.example.prspatientregistrationsystem.core.visit.VisitService;
import org.example.prspatientregistrationsystem.core.service.ServiceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final VisitService visitService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public PaymentService(PaymentRepository paymentRepository, RestTemplate restTemplate, VisitService visitService) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.visitService = visitService;
        this.objectMapper.findAndRegisterModules();
    }

    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequest) {
        try {
            log.info("Creating payment for visit: {}", paymentRequest.getVisitId());
            
            // Generuje unikalny ID płatności
            String paymentId = UUID.randomUUID().toString();
            
            // Zapisuje dane płatności w bazie
            PaymentEntity paymentEntity = PaymentEntity.builder()
                    .paymentId(paymentId)
                    .visitId(paymentRequest.getVisitId())
                    .patientName(paymentRequest.getPatientName())
                    .patientEmail(paymentRequest.getPatientEmail())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .status("PENDING")
                    .description(paymentRequest.getDescription())
                    .doctorName(paymentRequest.getDoctorName())
                    .visitDate(paymentRequest.getVisitDate())
                    .visitDescription(paymentRequest.getVisitDescription())
                    .selectedServices(paymentRequest.getSelectedServices())
                    .build();
            
            log.info("Saving payment entity with selectedServices: {}", paymentEntity.getSelectedServices());
            paymentRepository.save(paymentEntity);
            log.info("Payment entity saved with ID: {}", paymentEntity.getId());
            
            // Pobiera token OAuth2 do autoryzacji z PayU
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("Failed to get PayU access token");
                throw new RuntimeException("Failed to authenticate with PayU");
            }
            log.info("PayU access token obtained successfully");
            
            // Prepare PayU request
            Map<String, Object> payuRequest = createPayURequest(paymentRequest, paymentId);
            log.info("PayU request prepared: {}", payuRequest);
            
            // Wysyła żądanie do PayU API
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
        
       
        request.put("notifyUrl", notifyUrl);
        request.put("customerIp", "127.0.0.1");
        request.put("merchantPosId", posId);
        request.put("description", paymentRequest.getDescription());
        request.put("currencyCode", paymentRequest.getCurrency());
        
        // Adresy powrotu po płatności
        if (paymentRequest.getSuccessUrl() != null) {
            request.put("continueUrl", paymentRequest.getSuccessUrl());
        }
        if (paymentRequest.getFailureUrl() != null) {
            request.put("failureUrl", paymentRequest.getFailureUrl());
        }
        
   
        int amountInCents = paymentRequest.getAmount().multiply(new BigDecimal("100")).intValue();
        request.put("totalAmount", amountInCents);
        
       
        request.put("extOrderId", paymentId);
        
      
        Map<String, Object> buyer = new HashMap<>();
        buyer.put("email", paymentRequest.getPatientEmail());
        
       
        String[] nameParts = paymentRequest.getPatientName().split(" ", 2);
        buyer.put("firstName", nameParts[0]);
        buyer.put("lastName", nameParts.length > 1 ? nameParts[1] : "");
        buyer.put("language", "pl");
        request.put("buyer", buyer);
        
        // Lista produktów 
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
            
           
            String tokenUrl = payuApiUrl + "/pl/standard/user/oauth/authorize";
            log.info("Token URL: {}", tokenUrl);
            
            // Tworzy dane formularza
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
            
            // Jeśli płatność się powiodła, tworzy wizytę
            if ("COMPLETED".equals(status) || "SUCCESS".equals(status)) {
                createVisitFromPayment(payment);
            }
            
        } catch (Exception e) {
            log.error("Error processing payment callback", e);
            throw e;
        }
    }
    
    private void createVisitFromPayment(PaymentEntity payment) {
        try {
            log.info("Creating visit from successful payment: {}", payment.getPaymentId());
            log.info("Payment data: doctorName={}, patientName={}, visitDate={}, visitDescription={}, selectedServices={}, amount={}", 
                    payment.getDoctorName(), payment.getPatientName(), payment.getVisitDate(), 
                    payment.getVisitDescription(), payment.getSelectedServices(), payment.getAmount());
            
            // Walidacja danych płatności
            if (payment.getDoctorName() == null || payment.getPatientName() == null) {
                log.error("Payment data is incomplete: doctorName={}, patientName={}", 
                        payment.getDoctorName(), payment.getPatientName());
                return;
            }
            
          
            List<ServiceDto> selectedServices = parseSelectedServices(payment.getSelectedServices());
            log.info("Parsed selected services: {}", selectedServices);
            
            // Tworzy DTO wizyty z danych płatności
            VisitDto visitDto = VisitDto.builder()
                    .doctorName(payment.getDoctorName())
                    .patient(payment.getPatientName())
                    .date(parseVisitDate(payment.getVisitDate()))
                    .description(payment.getVisitDescription() != null ? payment.getVisitDescription() : "")
                    .selectedServices(selectedServices)
                    .totalCost(payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO)
                    .build();
            
            log.info("Created VisitDto: {}", visitDto);
            
            // Zapisuje wizytę w systemie
            Long visitId = visitService.addVisit(visitDto);
            log.info("Visit created successfully with ID: {}", visitId);
            
        } catch (Exception e) {
            log.error("Error creating visit from payment", e);
        }
    }
    
    private List<ServiceDto> parseSelectedServices(String selectedServices) {
        try {
            if (selectedServices != null && !selectedServices.isEmpty() && !"null".equals(selectedServices)) {
                log.info("Parsing selected services in PaymentService: {}", selectedServices);
                return objectMapper.readValue(selectedServices, new TypeReference<List<ServiceDto>>() {});
            } else {
                log.info("Selected services is null or empty in PaymentService, returning null");
                return null;
            }
        } catch (Exception e) {
            log.error("Error parsing selected services in PaymentService: {}", e.getMessage());
            log.error("Selected services string was: '{}'", selectedServices);
            return null;
        }
    }
    
    private LocalDateTime parseVisitDate(String dateString) {
        try {
            log.info("Parsing visit date in PaymentService: '{}'", dateString);
            
            if (dateString != null && !dateString.isEmpty() && !"null".equals(dateString)) {
               
                DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                };
                
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        LocalDateTime parsedDate = LocalDateTime.parse(dateString, formatter);
                        log.info("Successfully parsed date in PaymentService: {} using formatter: {}", parsedDate, formatter);
                        return parsedDate;
                    } catch (Exception ignored) {
                        log.debug("Failed to parse date with formatter in PaymentService: {}", formatter);
                    }
                }
                
                log.warn("Could not parse date with any formatter in PaymentService, using current time");
                return LocalDateTime.now();
            } else {
                log.info("Date string is null or empty in PaymentService, using current time");
                return LocalDateTime.now(); 
            }
        } catch (Exception e) {
            log.error("Error parsing visit date in PaymentService: {}", e.getMessage());
            return LocalDateTime.now();
        }
    }
    
    public Map<String, Object> getPaymentStatus(String paymentId) {
        try {
            PaymentEntity payment = paymentRepository.findByPaymentId(paymentId)
                    .orElse(null);
            
            if (payment == null) {
                log.warn("Payment not found for paymentId: {}", paymentId);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Payment not found");
                error.put("status", "NOT_FOUND");
                return error;
            }
            
            Map<String, Object> status = new HashMap<>();
            status.put("paymentId", payment.getPaymentId());
            status.put("status", payment.getStatus());
            status.put("amount", payment.getAmount());
            status.put("createdAt", payment.getCreatedAt());
            status.put("updatedAt", payment.getUpdatedAt());
            
            log.info("Payment status for {}: {}", paymentId, payment.getStatus());
            return status;
            
        } catch (Exception e) {
            log.error("Error getting payment status for paymentId: {}", paymentId, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("status", "ERROR");
            return error;
        }
    }
    
    public Map<String, Object> getPaymentStatusByVisitId(String visitId) {
        try {
            log.info("Getting payment status by visitId: {}", visitId);
            PaymentEntity payment = paymentRepository.findByVisitId(visitId)
                    .orElse(null);
            
            if (payment == null) {
                log.warn("Payment not found for visitId: {}", visitId);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Payment not found");
                error.put("status", "NOT_FOUND");
                return error;
            }
            
            // Jeśli płatność jest pending, sprawdza status w PayU
            if ("PENDING".equals(payment.getStatus())) {
                log.info("Payment is pending, checking status in PayU for paymentId: {}", payment.getPaymentId());
                String payuStatus = checkPaymentStatusInPayU(payment.getPaymentId());
                if (payuStatus != null && !"PENDING".equals(payuStatus)) {
                    payment.setStatus(payuStatus);
                    paymentRepository.save(payment);
                    log.info("Updated payment status to: {}", payuStatus);
                }
            }
            
            Map<String, Object> status = new HashMap<>();
            status.put("paymentId", payment.getPaymentId());
            status.put("visitId", payment.getVisitId());
            status.put("status", payment.getStatus());
            status.put("amount", payment.getAmount());
            status.put("createdAt", payment.getCreatedAt());
            status.put("updatedAt", payment.getUpdatedAt());
            
            log.info("Payment status for visitId {}: {}", visitId, payment.getStatus());
            return status;
            
        } catch (Exception e) {
            log.error("Error getting payment status for visitId: {}", visitId, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("status", "ERROR");
            return error;
        }
    }
    
    private String checkPaymentStatusInPayU(String paymentId) {
        try {
            log.info("Checking payment status in PayU for paymentId: {}", paymentId);
            
            // Pobiera token dostępu
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("Failed to get PayU access token for status check");
                return null;
            }
            
            // Przygotowuje żądanie do PayU
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Sprawdza status w PayU
            String statusUrl = payuApiUrl + "/api/v2_1/orders/" + paymentId;
            log.info("Checking status at: {}", statusUrl);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    statusUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            
            log.info("PayU status response: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> payuResponse = response.getBody();
                
                // PayU zwraca status w zagnieżdżonej strukturze

                if (payuResponse.containsKey("orders") && payuResponse.get("orders") instanceof List) {
                    List<Map<String, Object>> orders = (List<Map<String, Object>>) payuResponse.get("orders");
                    if (!orders.isEmpty()) {
                        Map<String, Object> order = orders.get(0);
                        if (order.containsKey("status")) {
                            String status = (String) order.get("status");
                            log.info("PayU returned status: {}", status);
                            return status;
                        }
                    }
                }
                
                // Fallback: sprawdza czy status jest bezpośrednio w odpowiedzi
                if (payuResponse.containsKey("status")) {
                    Object statusObj = payuResponse.get("status");
                    if (statusObj instanceof String) {
                        String status = (String) statusObj;
                        log.info("PayU returned status (direct): {}", status);
                        return status;
                    } else if (statusObj instanceof Map) {
                        Map<String, Object> statusMap = (Map<String, Object>) statusObj;
                        if (statusMap.containsKey("statusCode")) {
                            String status = (String) statusMap.get("statusCode");
                            log.info("PayU returned status (from statusCode): {}", status);
                            return status;
                        }
                    }
                }
                
                log.warn("Could not find status in PayU response structure");
                return null;
            } else {
                log.warn("PayU status check failed: status={}, body={}", response.getStatusCode(), response.getBody());
                return null;
            }
            
        } catch (Exception e) {
            log.error("Error checking payment status in PayU", e);
            return null;
        }
    }
    
    public PaymentEntity getPaymentEntity(String paymentId) {
        try {
            return paymentRepository.findByPaymentId(paymentId)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error getting payment entity", e);
            return null;
        }
    }
    
    public PaymentEntity getPaymentEntityByVisitId(String visitId) {
        try {
            log.info("Looking for payment entity by visitId: {}", visitId);
            PaymentEntity payment = paymentRepository.findByVisitId(visitId)
                    .orElse(null);
            log.info("Found payment entity: {}", payment);
            return payment;
        } catch (Exception e) {
            log.error("Error getting payment entity by visitId", e);
            return null;
        }
    }

    public PaymentEntity getPaymentByVisitId(String visitId) {
        try {
            log.info("Getting payment by visitId: {}", visitId);
            return paymentRepository.findByVisitId(visitId)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error getting payment by visitId", e);
            return null;
        }
    }
} 
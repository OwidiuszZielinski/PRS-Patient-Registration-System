package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.payment.PaymentService;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.example.prspatientregistrationsystem.core.payment.PaymentRepository;
import org.example.prspatientregistrationsystem.core.payment.PaymentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentRequestDto paymentRequest;
    private PaymentResponseDto paymentResponse;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequestDto.builder()
                .visitId("123")
                .patientName("John Doe")
                .patientEmail("john@example.com")
                .amount(new BigDecimal("100.00"))
                .description("Medical visit")
                .currency("PLN")
                .build();

        paymentResponse = PaymentResponseDto.builder()
                .paymentId("payu-123")
                .redirectUrl("https://secure.snd.payu.com/pay/123")
                .status("SUCCESS")
                .message("Payment created successfully")
                .build();
    }

    @Test
    void createPayment_ShouldReturnSuccessResponse() {
        when(paymentService.createPayment(paymentRequest)).thenReturn(paymentResponse);

        ResponseEntity<PaymentResponseDto> response = paymentController.createPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(paymentResponse, response.getBody());
        verify(paymentService, times(1)).createPayment(paymentRequest);
    }

    @Test
    void createPayment_ShouldReturnBadRequest_WhenExceptionOccurs() {
        when(paymentService.createPayment(paymentRequest)).thenThrow(new RuntimeException("Payment error"));

        ResponseEntity<PaymentResponseDto> response = paymentController.createPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void handlePaymentCallback_ShouldReturnOK_WhenSuccessful() {
        Map<String, Object> callbackData = new HashMap<>();
        callbackData.put("orderId", "123");
        callbackData.put("status", "COMPLETED");

        doNothing().when(paymentService).handlePaymentCallback(callbackData);

        ResponseEntity<String> response = paymentController.handlePaymentCallback(callbackData);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("OK", response.getBody());
        verify(paymentService, times(1)).handlePaymentCallback(callbackData);
    }

    @Test
    void handlePaymentCallback_ShouldReturnBadRequest_WhenExceptionOccurs() {
        Map<String, Object> callbackData = new HashMap<>();
        callbackData.put("orderId", "123");

        doThrow(new RuntimeException("Callback error")).when(paymentService).handlePaymentCallback(callbackData);

        ResponseEntity<String> response = paymentController.handlePaymentCallback(callbackData);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("ERROR", response.getBody());
    }

    @Test
    void getPaymentStatus_ShouldReturnStatus_WhenPaymentExists() {
        String paymentId = "123";
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentId(paymentId);
        payment.setStatus("COMPLETED");
        payment.setAmount(new java.math.BigDecimal("100.00"));
        payment.setVisitId("visit-1");
        payment.setPatientName("John Doe");

        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(java.util.Optional.of(payment));

        ResponseEntity<Map<String, Object>> response = paymentController.getPaymentStatus(paymentId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(paymentId, response.getBody().get("paymentId"));
        assertEquals("COMPLETED", response.getBody().get("status"));
        verify(paymentRepository, times(1)).findByPaymentId(paymentId);
    }

    @Test
    void getPaymentStatus_ShouldReturnInternalServerError_WhenExceptionOccurs() {
        String paymentId = "123";
        when(paymentRepository.findByPaymentId(paymentId)).thenThrow(new RuntimeException("Status error"));

        ResponseEntity<Map<String, Object>> response = paymentController.getPaymentStatus(paymentId);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("error"));
    }
} 
package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.core.payment.PaymentService;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

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
        Map<String, Object> status = new HashMap<>();
        status.put("paymentId", paymentId);
        status.put("status", "COMPLETED");

        when(paymentService.getPaymentStatus(paymentId)).thenReturn(status);

        ResponseEntity<Map<String, Object>> response = paymentController.getPaymentStatus(paymentId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(status, response.getBody());
        verify(paymentService, times(1)).getPaymentStatus(paymentId);
    }

    @Test
    void getPaymentStatus_ShouldReturnBadRequest_WhenExceptionOccurs() {
        String paymentId = "123";

        when(paymentService.getPaymentStatus(paymentId)).thenThrow(new RuntimeException("Status error"));

        ResponseEntity<Map<String, Object>> response = paymentController.getPaymentStatus(paymentId);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
} 
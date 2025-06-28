package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.payment.PaymentEntity;
import org.example.prspatientregistrationsystem.core.payment.PaymentRepository;
import org.example.prspatientregistrationsystem.core.payment.PaymentService;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentRequestDto;
import org.example.prspatientregistrationsystem.core.payment.dto.PaymentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequest) {
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handlePaymentCallback(@RequestBody Map<String, Object> callbackData) {
        paymentService.handlePaymentCallback(callbackData);
        return ResponseEntity.ok().build();
    }

    // Pobiera status płatności po ID płatności
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable String paymentId) {
        var payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return ResponseEntity.ok(buildResponse(payment));
    }

    // Pobiera płatność po ID wizyty
    @GetMapping("/by-visit-id/{visitId}")
    public ResponseEntity<PaymentEntity> getPaymentByVisitId(@PathVariable String visitId) {
        var payment = paymentService.getPaymentByVisitId(visitId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }
        return ResponseEntity.ok(payment);
    }

    private static Map<String, Object> buildResponse(PaymentEntity payment) {
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", payment.getPaymentId());
        response.put("status", payment.getStatus());
        response.put("amount", payment.getAmount());
        response.put("visitId", payment.getVisitId());
        response.put("patientName", payment.getPatientName());
        return response;
    }
} 
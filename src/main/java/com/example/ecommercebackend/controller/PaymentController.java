package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.PaymentDto;
import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.request.AddPaymentRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.dto.response.PaymentIntentResponse;
import com.example.ecommercebackend.service.payment.IPaymentService;
import com.example.ecommercebackend.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/payments")
public class PaymentController {

    private final IPaymentService paymentService;
    private final IUserService userService;

    @PostMapping("/create-intent")
    public ResponseEntity<ApiResponse> processPayment(@Valid @RequestBody AddPaymentRequest request) {
        try {
            UserDto user = userService.getAuthenticatedUser();
            PaymentIntentResponse response = paymentService.processPayment(user.getId(), request);
            return ResponseEntity.ok(new ApiResponse("Payment intent created", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/my-payments")
    public ResponseEntity<ApiResponse> getUserPayments() {
        try {
            UserDto user = userService.getAuthenticatedUser();
            List<PaymentDto> payments = paymentService.getUserPayments(user.getId());
            return ResponseEntity.ok(new ApiResponse("Payments retrieved", payments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> getPaymentById(@PathVariable UUID paymentId) {
        try {
            UserDto user = userService.getAuthenticatedUser();
            PaymentDto payment = paymentService.getPaymentById(paymentId, user.getId());
            return ResponseEntity.ok(new ApiResponse("Payment retrieved", payment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(new ApiResponse("Payments retrieved", payments));
    }

    @PatchMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updatePaymentStatus(
            @PathVariable UUID paymentId,
            @RequestParam String status
    ) {
        try {
            PaymentDto payment = paymentService.updatePaymentStatus(paymentId, status);
            return ResponseEntity.ok(new ApiResponse("Payment status updated to " + status, payment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invalid status provided", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse> cancelPayment(@PathVariable UUID paymentId) {
        try {
            UserDto user = userService.getAuthenticatedUser();
            paymentService.cancelPayment(paymentId, user.getId());
            return ResponseEntity.ok(new ApiResponse("Payment cancelled successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            paymentService.handleWebhookEvent(payload, sigHeader);
        } catch (Exception e) {
            log.error("Stripe webhook error: {}", e.getMessage());
        }
        return ResponseEntity.ok("");
    }
}

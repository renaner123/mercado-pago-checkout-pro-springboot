package com.mercado_pago.checkout.service;

import com.mercado_pago.checkout.client.MercadoPagoClient;
import com.mercado_pago.checkout.model.entity.Payment;
import com.mercado_pago.checkout.model.dto.ProcessPaymentNotificationRequestDTO;
import com.mercado_pago.checkout.model.dto.ProcessPaymentNotificationResponseDTO;
import com.mercado_pago.checkout.exceptions.PaymentGatewayException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class ProcessPaymentNotificationService {

    private final MercadoPagoClient mercadoPagoClient;

    /**
     * Processes a payment notification by fetching the payment details from the gateway
     * and updating the order status based on the payment status.
     *
     * @param input The request data containing the resource type and ID.
     * @return A response indicating success or failure, along with the updated payment status.
     */
    public ProcessPaymentNotificationResponseDTO processPaymentNotification(ProcessPaymentNotificationRequestDTO input) {
        log.info("Executing ProcessPaymentNotificationUseCase for resource type: {}, ID: {}",
                input.resourceType(), input.resourceId());

        ProcessPaymentNotificationResponseDTO INVALID_NOTIFICATION = validateInput(input);
        if (INVALID_NOTIFICATION != null) return INVALID_NOTIFICATION;

        String paymentId = input.resourceId();

        try {
            log.debug("Fetching payment details for ID: {}", paymentId);

            // 1. Fetch the actual payment details from Mercado Pago using the ID
            Payment paymentDetails = mercadoPagoClient.getPaymentDetails(paymentId);

            log.info("Fetched payment details for ID: {}. Status: {}", paymentId, paymentDetails.getStatus());

            // 2. Process the payment status (e.g., update order status in your database, delete cart if approved)
            Payment.PaymentStatus updatedStatus = getPaymentStatus(paymentDetails);

            // 3. Return success and the updated status
            return new ProcessPaymentNotificationResponseDTO(true, updatedStatus.name());

        } catch (PaymentGatewayException e) {
            log.error("Error fetching payment details from gateway for payment ID {}: {}", paymentId, e.getMessage(), e);
            // Depending on the error, you might want to return success=false to trigger retries,
            // but be cautious of infinite retry loops.
            return new ProcessPaymentNotificationResponseDTO(false, "GATEWAY_ERROR");
        } catch (Exception e) {
            log.error("Unexpected error processing notification for payment ID {}: {}", paymentId, e.getMessage(), e);
            return new ProcessPaymentNotificationResponseDTO(false, "PROCESSING_ERROR");
        }
    }

    private static Payment.PaymentStatus getPaymentStatus(Payment paymentDetails) {
        return paymentDetails.getStatus() != null
                ? paymentDetails.getStatus() : Payment.PaymentStatus.UNKNOWN;
    }

    private static ProcessPaymentNotificationResponseDTO validateInput(ProcessPaymentNotificationRequestDTO input) {
        if (input == null || input.resourceId() == null || !"payment".equalsIgnoreCase(input.resourceType())) {
            log.warn("Invalid or non-payment notification received. Type: {}, ID: {}",
                    input.resourceType(), input.resourceId());
            // It's often better to return success (HTTP 200/201) and log the issue.
            return new ProcessPaymentNotificationResponseDTO(false, "INVALID_NOTIFICATION");
        }
        return null;
    }
}
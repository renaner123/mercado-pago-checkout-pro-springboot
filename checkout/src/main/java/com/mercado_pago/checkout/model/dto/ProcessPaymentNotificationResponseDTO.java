package com.mercado_pago.checkout.model.dto;

public record ProcessPaymentNotificationResponseDTO(
        boolean success,
        String updatedStatus // e.g., "APPROVED", "REJECTED"
) {}

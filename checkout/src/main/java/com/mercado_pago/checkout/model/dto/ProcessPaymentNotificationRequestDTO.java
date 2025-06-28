package com.mercado_pago.checkout.model.dto;

public record ProcessPaymentNotificationRequestDTO (
    String resourceType, // e.g., "payment"
    String resourceId    // e.g., the payment ID
) {}


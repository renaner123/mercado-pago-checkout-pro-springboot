package com.mercado_pago.checkout.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private String id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String statusDetail;
    private PaymentMethod paymentMethod;
    private Payer payer;

    /**
     * Enum representing possible payment statuses.
     * Based on Mercado Pago statuses.
     */
    public enum PaymentStatus {
        PENDING,
        APPROVED,
        AUTHORIZED,
        IN_PROCESS,
        IN_MEDIATION,
        REJECTED,
        CANCELLED,
        REFUNDED,
        CHARGED_BACK,
        UNKNOWN,

    }

    /**
     * Enum representing possible payment methods.
     * Simplified mapping.
     */
    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PIX,
        BOLETO,
        ACCOUNT_MONEY,
        OTHER
    }
}

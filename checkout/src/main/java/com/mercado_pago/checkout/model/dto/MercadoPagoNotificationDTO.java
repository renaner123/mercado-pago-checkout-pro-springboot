package com.mercado_pago.checkout.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MercadoPagoNotificationDTO {

    private String action; // e.g., "payment.updated", "payment.created"

    @JsonProperty("api_version")
    private String apiVersion;

    private NotificationData data;

    @JsonProperty("date_created")
    private String dateCreated;

    private long id; // Notification ID

    @JsonProperty("live_mode")
    private boolean liveMode;

    private String type; // e.g., "payment"

    @JsonProperty("user_id")
    private long userId;

    @Data
    @NoArgsConstructor
    public static class NotificationData {
        private String id; // ID of the resource (e.g., payment ID)
    }
}


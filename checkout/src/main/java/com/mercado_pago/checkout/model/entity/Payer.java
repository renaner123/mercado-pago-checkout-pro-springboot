package com.mercado_pago.checkout.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payer {
    private String email;
    private String firstName;
    private String lastName;
    private Identification identification;

    @Data
    @Builder
    public static class Identification {
        private String type; // e.g., "CPF", "CNPJ"
        private String number;
    }
}

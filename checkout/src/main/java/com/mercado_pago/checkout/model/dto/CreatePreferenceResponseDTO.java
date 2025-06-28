package com.mercado_pago.checkout.model.dto;

public record CreatePreferenceResponseDTO(
        String preferenceId,
        String redirectUrl // The Mercado Pago init_point URL
) {}

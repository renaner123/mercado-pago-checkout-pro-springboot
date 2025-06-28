package com.mercado_pago.checkout.controller;

import com.mercado_pago.checkout.model.dto.CreatePreferenceRequestDTO;
import com.mercado_pago.checkout.model.dto.CreatePreferenceResponseDTO;
import com.mercado_pago.checkout.service.CreatePaymentPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentPreferenceController {
    private final CreatePaymentPreferenceService service;

    @PostMapping()
    public ResponseEntity<CreatePreferenceResponseDTO> createPreference(
          @Valid @RequestBody CreatePreferenceRequestDTO requestDTO) {
        log.info("Received request to create payment preference for orderId: {}", requestDTO.userId());

        try {
            CreatePreferenceResponseDTO useCaseOutput = service.createPreference(requestDTO);

            CreatePreferenceResponseDTO responseDTO = new CreatePreferenceResponseDTO(
                    useCaseOutput.preferenceId(),
                    useCaseOutput.redirectUrl()
            );
            return ResponseEntity.ok(responseDTO);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request data for creating preference: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // Or return an error response DTO
        } catch (Exception e) {
            log.error("Error creating payment preference for userId {}: {}", requestDTO.userId(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
}

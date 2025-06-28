package com.mercado_pago.checkout.service;

import com.mercado_pago.checkout.client.MercadoPagoClient;
import com.mercado_pago.checkout.model.dto.CreatePreferenceRequestDTO;
import com.mercado_pago.checkout.model.dto.CreatePreferenceResponseDTO;
import com.mercado_pago.checkout.exceptions.GenericBadRequestException;
import com.mercado_pago.checkout.exceptions.PaymentGatewayException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class CreatePaymentPreferenceService {

    private final MercadoPagoClient mercadoPagoClient;

    /**
     * Creates a payment preference using the provided input data.
     *
     * @param input The request data for creating the payment preference.
     * @return The response containing the created payment preference details.
     * @throws GenericBadRequestException if the input data is invalid.
     * @throws PaymentGatewayException if there is an error communicating with the payment gateway.
     */
    public CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO input) {
        log.info("Executing CreatePaymentPreferenceUseCase for userId: {}", input.userId());

        validateInput(input);

        // Here you would typically:
        // fetch the user to validate if it exists
        // fetch the address to validate if it exists or create a new one
        // fetch the cart to validate if it exists and belongs to the user
        // validate if the cart total matches the total in the database

        String orderNumber = genenateOrderNumber(input);

        try {
            // Delegate the preference creation to the payment gateway
            CreatePreferenceResponseDTO output = mercadoPagoClient.createPreference(input, orderNumber);
            log.info("Payment preference created successfully. PreferenceId: {}", output.preferenceId());
            return output;
        } catch (PaymentGatewayException e) {
            log.error("Error creating payment preference via gateway for orderId {}: {}", orderNumber, e.getMessage(), e);
            // Re-throw or handle specific gateway exceptions as needed
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during payment preference creation for orderId {}: {}", orderNumber, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while creating the payment preference.", e);
        }
    }

    private void validateInput(CreatePreferenceRequestDTO input) {
        validateBasicFields(input);
        validatePayer(input.payer());
        validateAddress(input.deliveryAddress());
        validateItems(input.items());
    }

    private void validateBasicFields(CreatePreferenceRequestDTO input) {
        if (Objects.isNull(input.userId()) ||
                Objects.isNull(input.totalAmount()) ||
                Objects.isNull(input.items()) || input.items().isEmpty()) {
            throw new GenericBadRequestException("Missing basic input fields for creating preference.");
        }
    }

    private void validatePayer(CreatePreferenceRequestDTO.PayerDTO payer) {
        if (Objects.isNull(payer) ||
                StringUtils.isBlank(payer.name()) ||
                StringUtils.isBlank(payer.email())) {
            throw new GenericBadRequestException("Invalid payer information.");
        }
    }

    private void validateAddress(CreatePreferenceRequestDTO.DeliveryAddressDTO address) {
        if (Objects.isNull(address) ||
                StringUtils.isBlank(address.street()) ||
                StringUtils.isBlank(address.city()) ||
                StringUtils.isBlank(address.neighborhood()) ||
                StringUtils.isBlank(address.number()) ||
                StringUtils.isBlank(address.zipCode())) {
            throw new GenericBadRequestException("Invalid delivery address.");
        }
    }

    private void validateItems(List<CreatePreferenceRequestDTO.ItemDTO> items) {
        for (int i = 0; i < items.size(); i++) {
            CreatePreferenceRequestDTO.ItemDTO item = items.get(i);
            if (Objects.isNull(item)) {
                throw new GenericBadRequestException("Item at index " + i + " is null.");
            }
            if (StringUtils.isBlank(item.title())) {
                throw new GenericBadRequestException("Item at index " + i + " has a blank title.");
            }
            if (Objects.isNull(item.quantity()) || item.quantity() <= 0) {
                throw new GenericBadRequestException("Item at index " + i + " has invalid quantity.");
            }
            if (Objects.isNull(item.unitPrice()) || item.unitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new GenericBadRequestException("Item at index " + i + " has invalid unit price.");
            }
        }
    }

    private static String genenateOrderNumber(CreatePreferenceRequestDTO input) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        String random = String.valueOf(System.currentTimeMillis() % 10000);
        return String.format("ORD-%d%s%s", input.userId(), date, random);
    }
}

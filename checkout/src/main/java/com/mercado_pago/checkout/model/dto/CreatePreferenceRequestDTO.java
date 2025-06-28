package com.mercado_pago.checkout.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreatePreferenceRequestDTO(
        Long userId,

        @DecimalMin(value = "0.01", message = "Total amount must be positive")
        BigDecimal totalAmount,

        @NotEmpty(message = "Items list cannot be empty")
        @Valid
        List<ItemDTO> items,

        @NotNull(message = "Payer information cannot be null")
        @Valid
        PayerDTO payer,

        @NotNull(message = "Back URLs cannot be null")
        @Valid
        BackUrlsDTO backUrls,

        @NotNull(message = "Delivery address cannot be null")
        @Valid
        DeliveryAddressDTO deliveryAddress,

        String notificationUrl
) {
    public record ItemDTO(
            @NotBlank(message = "Item ID cannot be blank")
            String id,
            @NotBlank(message = "Item title cannot be blank")
            String title,
            @NotNull(message = "Item quantity cannot be null")
            Integer quantity,
            @NotNull(message = "Item unit price cannot be null")
            @DecimalMin(value = "0.01", message = "Item unit price must be positive")
            BigDecimal unitPrice
    ) {}

    public record PayerDTO(
            @NotBlank(message = "Payer email cannot be blank")
            @Email(message = "Payer email must be valid")
            String email,
            @NotBlank(message = "Payer name cannot be blank")
            String name
            // Add other payer fields if needed
    ) {}

    public record BackUrlsDTO(
            @NotBlank(message = "Success URL cannot be blank")
            String success,
            @NotBlank(message = "Failure URL cannot be blank")
            String failure,
            @NotBlank(message = "Pending URL cannot be blank")
            String pending
    ) {}

    public record DeliveryAddressDTO(
            @NotBlank(message = "Street cannot be blank")
            String street,
            @NotBlank(message = "Number cannot be blank")
            String number,
            String complement,
            @NotBlank(message = "Neighborhood cannot be blank")
            String neighborhood,
            @NotBlank(message = "City cannot be blank")
            String city,
            @NotBlank(message = "State cannot be blank")
            String state,
            @NotBlank(message = "Zipcode cannot be blank")
            String zipCode
    ) {}
}

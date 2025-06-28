package com.mercado_pago.checkout.client;

import com.mercado_pago.checkout.model.dto.CreatePreferenceRequestDTO;
import com.mercado_pago.checkout.model.dto.CreatePreferenceResponseDTO;
import com.mercado_pago.checkout.model.entity.Payer;
import com.mercado_pago.checkout.model.entity.Payment;
import com.mercado_pago.checkout.exceptions.PaymentGatewayException;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoClient {

    @Value("${api.v1.mercadopago-access-token}")
    private String accessToken;

    /**
     * Initializes the Mercado Pago SDK with the provided access token.
     * This method is called after the bean is constructed to ensure the SDK is ready for use.
     */
    @PostConstruct
    public void init() {
        // Initialize Mercado Pago SDK with Access Token
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Mercado Pago SDK initialized successfully.");
    }

    /**
     * Creates a payment preference in Mercado Pago.
     *
     * @param input        The request data for creating the preference.
     * @param orderNumber  The order number to link with the preference.
     * @return A DTO containing the created preference ID and redirect URL.
     * @throws PaymentGatewayException If there is an error during the creation process.
     */
    public CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO input, String orderNumber) throws PaymentGatewayException {
        log.info("Creating Mercado Pago preference for orderNumber: {}", orderNumber);
        try {
            PreferenceClient client = new PreferenceClient();

            List<PreferenceItemRequest> items = input.items().stream()
                    .map(itemInput -> PreferenceItemRequest.builder()
                            .id(itemInput.id())
                            .title(itemInput.title())
                            .quantity(itemInput.quantity())
                            .unitPrice(itemInput.unitPrice())
                            .build())
                    .collect(Collectors.toList());

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(input.payer().email())
                    .name(input.payer().name())
                    // Add other payer details if available in input
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(input.backUrls().success())
                    .failure(input.backUrls().failure())
                    .pending(input.backUrls().pending())
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .autoReturn("approved") // Automatically return after approved payment
                    .externalReference(orderNumber) // Link to your order ID
                    // Add expiration settings, excluded payment methods/types if needed
                    .build();

            // Create the preference using the Mercado Pago client
            Preference preference = client.create(request);

            log.info("Mercado Pago preference created. ID: {}, InitPoint: {}", preference.getId(), preference.getInitPoint());

            return new CreatePreferenceResponseDTO(preference.getId(), preference.getInitPoint());

        } catch (MPApiException e) {
            log.error("Mercado Pago API error creating preference for orderNumber {}: Status Code: {}, Response: {}",
                    orderNumber, e.getStatusCode(), e.getApiResponse().getContent(), e);
            throw new PaymentGatewayException("Mercado Pago API error: " + e.getApiResponse().getContent(), e);
        } catch (MPException e) {
            log.error("Mercado Pago SDK error creating preference for orderNumber {}: {}", orderNumber, e.getMessage(), e);
            throw new PaymentGatewayException("Mercado Pago SDK error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error creating Mercado Pago preference for orderNumber {}: {}", orderNumber, e.getMessage(), e);
            throw new PaymentGatewayException("Unexpected error creating preference.", e);
        }
    }

    /**
     * Retrieves payment details from Mercado Pago using the payment ID.
     *
     * @param paymentId The ID of the payment to retrieve.
     * @return A Payment object containing the payment details.
     * @throws PaymentGatewayException If there is an error retrieving the payment details.
     */
    public Payment getPaymentDetails(String paymentId) throws PaymentGatewayException {
        log.info("Getting Mercado Pago payment details for payment ID: {}", paymentId);
        try {
            PaymentClient client = new PaymentClient();
            com.mercadopago.resources.payment.Payment mpPayment = client.get(Long.parseLong(paymentId));

            if (mpPayment == null) {
                log.warn("Payment not found in Mercado Pago for ID: {}", paymentId);
                throw new PaymentGatewayException("Payment not found with ID: " + paymentId);
            }

            log.debug("Raw payment details from MP: {}", mpPayment);

            // Map Mercado Pago Payment resource to our domain Payment model
            Payment.PaymentStatus status = mapMercadoPagoStatus(Payment.PaymentStatus.valueOf(mpPayment.getStatus().toUpperCase()));
            Payment.PaymentMethod method = mapMercadoPagoMethod(mpPayment.getPaymentMethodId());

            Payer payer = null;
            if (mpPayment.getPayer() != null) {
                Payer.Identification identification = null;
                if (mpPayment.getPayer().getIdentification() != null) {
                    identification = Payer.Identification.builder()
                            .type(mpPayment.getPayer().getIdentification().getType())
                            .number(mpPayment.getPayer().getIdentification().getNumber())
                            .build();
                }
                payer = Payer.builder()
                        .email(mpPayment.getPayer().getEmail())
                        .firstName(mpPayment.getPayer().getFirstName())
                        .lastName(mpPayment.getPayer().getLastName())
                        .identification(identification)
                        .build();
            }

            return Payment.builder()
                    .id(mpPayment.getId().toString())
                    .orderId(mpPayment.getExternalReference()) // Assuming externalReference holds our order ID
                    .amount(mpPayment.getTransactionAmount())
                    .paymentMethod(method)
                    .status(status)
                    .statusDetail(mpPayment.getStatusDetail())
                    .payer(payer)
                    .build();

        } catch (NumberFormatException e) {
            log.error("Invalid payment ID format: {}", paymentId, e);
            throw new PaymentGatewayException("Invalid payment ID format.", e);
        } catch (MPApiException e) {
            log.error("Mercado Pago API error getting payment details for ID {}: Status Code: {}, Response: {}",
                    paymentId, e.getStatusCode(), e.getApiResponse().getContent(), e);
            // Handle specific errors like 404 Not Found
            if (e.getStatusCode() == 404) {
                throw new PaymentGatewayException("Payment not found with ID: " + paymentId, e);
            }
            throw new PaymentGatewayException("Mercado Pago API error: " + e.getApiResponse().getContent(), e);
        } catch (MPException e) {
            log.error("Mercado Pago SDK error getting payment details for ID {}: {}", paymentId, e.getMessage(), e);
            throw new PaymentGatewayException("Mercado Pago SDK error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error getting Mercado Pago payment details for ID {}: {}", paymentId, e.getMessage(), e);
            throw new PaymentGatewayException("Unexpected error getting payment details.", e);
        }
    }

    // --- Private Helper Methods ---
    // These mappers should ideally be placed in a separate mapper class for better separation of concerns.

    private Payment.PaymentStatus mapMercadoPagoStatus(Payment.PaymentStatus status) {
        if (status == null) return Payment.PaymentStatus.UNKNOWN;

        return switch (status) {
            case APPROVED -> Payment.PaymentStatus.APPROVED;
            case PENDING -> Payment.PaymentStatus.PENDING;
            case AUTHORIZED -> Payment.PaymentStatus.AUTHORIZED;
            case IN_PROCESS -> Payment.PaymentStatus.IN_PROCESS;
            case IN_MEDIATION -> Payment.PaymentStatus.IN_MEDIATION;
            case REJECTED -> Payment.PaymentStatus.REJECTED;
            case CANCELLED -> Payment.PaymentStatus.CANCELLED;
            case REFUNDED -> Payment.PaymentStatus.REFUNDED;
            case CHARGED_BACK -> Payment.PaymentStatus.CHARGED_BACK;
            default -> Payment.PaymentStatus.UNKNOWN;
        };
    }

    private Payment.PaymentMethod mapMercadoPagoMethod(String methodId) {
        if (methodId == null) return null; // Or a default/unknown type
        // Basic mapping, can be expanded based on actual method IDs used
        if (methodId.startsWith("pix")) {
            return Payment.PaymentMethod.PIX;
        } else if (methodId.matches("visa|master|amex|elo|hipercard|diners|discover|aura|jcb")) {
            // This is a simplification, Mercado Pago has specific IDs like 'master', 'visa'
            return Payment.PaymentMethod.CREDIT_CARD;
        } else if (methodId.equals("bolbradesco") || methodId.equals("pec")) {
            // Example for Boleto or PEC (Lotérica)
            // return Payment.PaymentMethod.BOLETO; // If you add Boleto later
            return null;
        }
        // Add mappings for other payment methods if needed
        return null; // Or Payment.PaymentMethod.OTHER
    }

}


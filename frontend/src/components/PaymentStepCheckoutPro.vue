<template>
  <div class="form-step-wrapper payment-step-pro">
    <h2 class="step-title">Complete Payment</h2>

    <div class="checkout-pro-info">
      <p class="info-text">
        You will be redirected to Mercado Pago's secure platform to complete your payment.
      </p>

      <div class="security-section">
        <img
            src="../../images/mercadopago.webp"
            alt="Secure payment with Mercado Pago"
            class="security-seal"
        />
        <span class="security-text">Your data is protected.</span>
      </div>

      <div v-if="isLoading" class="loading-indicator">
        <div class="spinner"></div>
        <span>Preparing your payment...</span>
      </div>

      <div v-if="error" class="error-message">
        <p>An error occurred while preparing your payment:</p>
        <p>{{ error }}</p>
        <button class="btn btn-secondary" @click="initiateCheckoutPro">Try Again</button>
      </div>
    </div>

    <div class="step-navigation-buttons">
      <button type="button" class="btn btn-secondary" @click="goBack" :disabled="isLoading">Back</button>
      <button
          type="button"
          class="btn btn-primary btn-mercado-pago"
          @click="initiateCheckoutPro"
          :disabled="isLoading || !!error"
      >
        <span v-if="isLoading">Please wait...</span>
        <span v-else>Pay with Mercado Pago</span>
      </button>
    </div>
  </div>
</template>

<script>
import paymentService from "@/services/payment.service";

export default {
  name: 'PaymentStepCheckoutPro',
  props: {
    orderSummary: {
      type: Object,
      default: () => ({
        personalInfo: {
          fullName: "Carinha que mora logo ali",
          email: "carinhalogoali@teste.com"
        }
      })
    },
    deliveryAddress: {
      type: Object,
      default: () => ({
        street: "Rua de Exemplo",
        number: "456",
        city: "Santa Catarina",
        uf: "SC",
        zipCode: "22222-000",
        complement: "Casa",
        neighborhood: "Logo ali"
      })
    }
  },
  data() {
    return {
      isLoading: false,
      error: null,
      preferenceId: null,
      redirectUrl: null,
      cartItems: [],
      totalPrice: 0
    };
  },
  mounted() {
    this.fetchCartItems();
    console.log('cartitems', this.cartItems)
    console.log('deliveryAddress', this.deliveryAddress);
  },
  computed: {
    userId() {
      return 1;
    },
    checkoutPayload() {
      return {
        userId: this.userId,
        totalAmount: this.totalPrice,
        items: this.cartItems?.map(item => ({
          id: item.recipe?.id || 'unknown',
          title: item.recipe?.name || 'Item',
          quantity: item.quantity || 1,
          unitPrice: item.recipe?.price || 0
        })) || [],
        deliveryAddress:{
          street: this.deliveryAddress.street || '',
          number: this.deliveryAddress.number || '',
          city: this.deliveryAddress.city || '',
          state: this.deliveryAddress.uf || '',
          zipCode: this.deliveryAddress.zipCode || '',
          complement: this.deliveryAddress.complement || '',
          neighborhood: this.deliveryAddress.neighborhood || ''
        },
        payer: {
          email: this.orderSummary.personalInfo?.email || '',
          name: this.orderSummary.personalInfo?.fullName || '',
        },
        backUrls: {
            success: `${window.location.origin}/checkout/success`,
            failure: `${window.location.origin}/checkout/failure`,
            pending: `${window.location.origin}/checkout/pending`
        },
        autoReturn: "approved"
      };
    }
  },
  methods: {
    async initiateCheckoutPro() {
      this.isLoading = true;
      this.error = null;
      this.redirectUrl = null;

      try {
        const response = await paymentService.createPreference(this.checkoutPayload);

        if (response.preferenceId && response.redirectUrl) {
          this.preferenceId = response.preferenceId;
          this.redirectUrl = response.redirectUrl;

          window.location.href = this.redirectUrl;

          this.$emit('preference-created', { preferenceId: this.preferenceId });
          
        } else {
          throw new Error('Could not obtain the payment link.')
        }

      } catch (err) {
        console.error('Error starting Checkout Pro:', err);
        this.error = err.response?.data?.message || err.message || 'Unknown error while preparing payment.';
        this.isLoading = false;
      }
    },

    goBack() {
      this.$emit('back');
    },
    async fetchCartItems() {
      this.cartItems = [
        {
          recipe: {
            id: '1',
            name: 'Produto Teste A',
            price: 1.0
          },
          quantity: 1
        },
        {
          recipe: {
            id: '2',
            name: 'Produto Teste B',
            price: 1.0
          },
          quantity: 2
        }
      ];

      this.totalPrice = this.cartItems.reduce((sum, item) => {
        return sum + item.quantity * item.recipe.price;
      }, 0);
    }
  }
};
</script>

<style scoped>
.payment-step-pro {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fff;
}

.step-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

.checkout-pro-info {
  text-align: center;
  padding: 20px 0;
}

.info-text {
  font-size: 1rem;
  color: #555;
  margin-bottom: 20px;
}

.security-section {
  margin: 25px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.security-seal {
  max-width: 300px;
  height: auto;
}

.security-text {
  font-size: 0.9rem;
  color: #6c757d;
}

.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
  color: #007bff;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(0, 123, 255, 0.3);
  border-radius: 50%;
  border-top-color: #007bff;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  margin-top: 15px;
  padding: 15px;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  color: #721c24;
  font-size: 0.9rem;
}

.error-message p {
  margin: 5px 0;
}

.step-navigation-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.btn {
  padding: 12px 25px;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0069d9;
}

.btn-primary:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

.btn-secondary:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

.btn-mercado-pago {
  background-color: #009ee3;
}

.btn-mercado-pago:hover {
  background-color: #0087cc;
}

@media (max-width: 576px) {
  .step-title {
    font-size: 1.3rem;
  }
  .btn {
    padding: 10px 15px;
    font-size: 0.9rem;
  }
}
</style>

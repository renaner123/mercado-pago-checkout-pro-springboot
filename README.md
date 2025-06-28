# 🛒 Mercado Pago Checkout Pro with Spring Boot

This project demonstrates how to integrate **Mercado Pago's Checkout Pro** using **Spring Boot**, focusing on:

- Creating payment preferences from the backend
- Redirecting the user to the secure Mercado Pago checkout
- Receiving and processing payment notifications via **Webhook**
- Testing with **sandbox accounts and cards**
- Local simulation using **ngrok**

> This project is part of a video tutorial published on YouTube.

---

## 🧱 Technologies Used

- Java 21
- Spring Boot 3.5
- Mercado Pago Official SDK
- Ngrok (for local HTTPS testing)
- Vue 3 (basic frontend for simulation)

---

## 🚀 Getting Started Locally

### 1. Clone the repository

```bash
git clone https://github.com/renaner123/mercado-pago-checkout-pro-springboot.git
cd mercado-pago-checkout-pro-springboot/checkout
```

### 2. Set your credentials

Create a `.env` file or configure environment variables with your test account's **Access Token**:

```
MERCADO_PAGO_ACCESS_TOKEN=TEST-xxxxxxxxxxxxxxxxxxxxx
```

In Spring Boot, this can be loaded in `application.properties` or accessed with `System.getenv()`.

### 3. Run the project

```bash
./mvnw spring-boot:run
```

---

## 📦 Endpoints

### Create Payment Preference

**POST** `/api/v1/payments`

Request payload example:

```json
{
  "userId": 1,
  "totalAmount": 119.7,
  "items": [
    {
      "id": "1",
      "title": "Test Product A",
      "quantity": 1,
      "unitPrice": 59.9
    },
    {
      "id": "2",
      "title": "Test Product B",
      "quantity": 2,
      "unitPrice": 29.9
    }
  ],
  "deliveryAddress": {
    "street": "Example Street",
    "number": "456",
    "city": "Santa Catarina",
    "state": "SC",
    "zipCode": "22222-000",
    "complement": "House",
    "neighborhood": "Nearby"
  },
  "payer": {
    "email": "buyer@example.com",
    "name": "Buyer Test"
  },
  "backUrls": {
    "success": "https://your-ngrok-url.ngrok-free.app/checkout/success",
    "failure": "https://your-ngrok-url.ngrok-free.app/checkout/failure",
    "pending": "https://your-ngrok-url.ngrok-free.app/checkout/pending"
  },
  "autoReturn": "approved"
}
```

Response:

```json
{
  "preferenceId": "152454022-90e74b33-d60f-4b0f-89a0-d9087097fcef",
  "redirectUrl": "https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=152454022-90e74b33-d60f-4b0f-89a0-d9087097fcef"
}
```

### Webhook

**POST** `/api/v1/webhooks/mercadopago`

> Mercado Pago will send automatic notifications to this endpoint with the `payment_id`.

---

## 🖥 Running the Frontend (Vue 3 + Vue CLI)

A basic Vue 3 frontend (optional) is included to simulate the checkout process. You can run it locally using the following steps:

### 1. Navigate to the frontend directory

```bash
cd frontend
```

### 2. Install dependencies

Ensure you have Node.js and npm installed.

```bash
npm install
```

### 3. Set the backend URL

Update the API base URL used by the frontend. Open the file:

```js
// frontend/src/constants.js
export const API_URL = process.env.VUE_APP_API_URL;
```

Then create a `.env` file in the frontend root:

```
VUE_APP_API_URL=https://your-backend-url.ngrok-free.app/api/v1
```

> 💡 This allows easy switching between local, staging, and production environments.

### 4. Run the development server

```bash
npm run serve
```

This will start the frontend on [http://localhost:8080](http://localhost:8080) by default.

---

## 🌐 Exposing Localhost with Ngrok

To test Mercado Pago's redirect URLs and webhook notifications in a local environment, expose your backend (and optionally frontend) using [ngrok](https://ngrok.com/).

Create a `ngrok.yml` file:

```yaml
version: "3"
agent:
  authtoken: YOUR_NGROK_AUTH_TOKEN
tunnels:
  frontend:
    proto: http
    addr: 8080
    host_header: rewrite
  backend:
    proto: http
    addr: 8090

```

Then run:

```bash
ngrok start --all
```

Copy the generated URLs (e.g. `https://abc123.ngrok.io`) and:

- Use it as your `VUE_APP_API_URL` in the frontend `.env`
- Configure the webhook in the [Mercado Pago Webhooks Panel](https://www.mercadopago.com.br/developers/panel/notifications):

```
https://abc123.ngrok.io/api/v1/webhooks/mercadopago
```

> ✅ Enable at least the **payment** event.

---

## ✅ Next Steps

- Persist order (`Order`, `OrderItem`) when creating a preference
- Update order status upon receiving approved payment notification
- Log all webhook requests received
- Add authentication/verification to webhook endpoint

---

## 📎 Useful Resources

- [Official Documentation – Checkout Pro](https://www.mercadopago.com.br/developers/en/reference)
- [Ngrok – Official Site](https://ngrok.com/)
- [Mercado SDK (Java)](https://github.com/mercadopago/sdk-java)

---

## 🧑‍💻 Author

**Renan Rodolfo**  
[LinkedIn](https://www.linkedin.com/in/renanrodolfo/)  
[YouTube Channel](https://www.youtube.com/@RenanRodolfoDev)

---

## 📝 License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.

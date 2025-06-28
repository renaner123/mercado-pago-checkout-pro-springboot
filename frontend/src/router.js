import { createWebHistory, createRouter } from "vue-router";
import Home from "./components/Home.vue";
import PaymentStepCheckoutPro from "@/components/PaymentStepCheckoutPro.vue";
import PaymentSuccess from "@/components/PaymentSuccess.vue";
import PaymentFailure from "@/components/PaymentFailure.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: Home,
  },
  {
    path: "/checkout",
    name: "checkout",
    component: PaymentStepCheckoutPro,
  },
  {
    path: "/checkout/success",
    name: "payment-success",
    component: PaymentSuccess,
  },
  {
    path: "/checkout/failure",
    name: "payment-failure",
    component: PaymentFailure,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});


export default router;
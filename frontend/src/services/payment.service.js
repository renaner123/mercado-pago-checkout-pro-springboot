import axios from 'axios';
import authHeader from "@/services/utils/auth-header";
import {API_URL} from "@/constants";

const URL = API_URL + '/payments';

class PaymentService {

    async createPreference(preference) {
        try {
            const response = await axios.post(URL, preference, { headers: authHeader() });
            return response.data;
        } catch (error) {
            console.error('Error create payments:', error);
            throw error;
        }
    }
}

export default new PaymentService();
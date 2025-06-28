export default function isTokenValid() {
    const token = localStorage.getItem('token');
    if (!token) return false;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const now = Math.floor(Date.now() / 1000); //
        return payload.exp && payload.exp > now;
    } catch (e) {
        return false;
    }
}
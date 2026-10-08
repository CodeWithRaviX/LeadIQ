import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor for unified error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const errorMsg = error.response?.data?.message || error.message || 'An unexpected error occurred';
    console.error('API Error:', errorMsg);
    return Promise.reject(new Error(errorMsg));
  }
);

export default api;

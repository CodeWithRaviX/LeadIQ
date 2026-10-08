import axios from 'axios';

const getBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL;
  if (!envUrl || !envUrl.trim()) {
    return '/api';
  }
  const cleanUrl = envUrl.trim().replace(/\/+$/, '');
  return cleanUrl.endsWith('/api') ? cleanUrl : `${cleanUrl}/api`;
};

const api = axios.create({
  baseURL: getBaseUrl(),
  timeout: 90000, // 90s to accommodate Render free-tier container cold starts
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor with automatic 1-time retry for Render cold starts
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const config = error.config;
    if (config && !config._retry && (error.code === 'ECONNABORTED' || error.message?.includes('timeout') || error.response?.status === 503)) {
      config._retry = true;
      console.warn('Backend cold start detected (Render). Retrying request...');
      await new Promise((resolve) => setTimeout(resolve, 3000));
      return api(config);
    }

    const errorMsg =
      error.code === 'ECONNABORTED' || error.message?.includes('timeout')
        ? 'Backend service is waking up from cold start. Please click Retry Connection in a moment.'
        : error.response?.data?.message || error.message || 'An unexpected error occurred';

    console.error('API Error:', errorMsg);
    return Promise.reject(new Error(errorMsg));
  }
);

export default api;

import axios from 'axios';

const API_BASE_URL = '/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 - 토큰 자동 추가
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 401 에러 시 로그아웃 처리
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 인증 관련 API
export const authAPI = {
  login: (loginId, password) => 
    apiClient.post('/User/login', { loginId, password }),
  
  signup: (userData) => 
    apiClient.post('/User/signup', userData),
  
  getMyInfo: () => 
    apiClient.get('/User/myFindOne'),
};

// 상품 관련 API
export const productAPI = {
  getAllProducts: () => 
    apiClient.get('/Product/findAll'),
  
  getProductById: (productId) => 
    apiClient.get(`/Product/findOne/${productId}`),
  
  getProductsByCategory: (categoryId) => 
    apiClient.get(`/Product/findByCategory/${categoryId}`),
};

// 카테고리 관련 API
export const categoryAPI = {
  getAllCategories: () => 
    apiClient.get('/ColumnCategory/findAll'),
  
  getSubCategories: (columnCategoryId) => 
    apiClient.get(`/RowCategory/findByColumn/${columnCategoryId}`),
};

// 주소 관련 API
export const addressAPI = {
  getMyAddresses: () => 
    apiClient.get('/Address/findAll'),
  
  addAddress: (addressData) => 
    apiClient.post('/Address/insert', addressData),
  
  updateAddress: (addressId, addressData) => 
    apiClient.put(`/Address/modify/${addressId}`, addressData),
  
  deleteAddress: (addressId) => 
    apiClient.delete(`/Address/delete/${addressId}`),
};

// 결제 수단 관련 API
export const paymentAPI = {
  getMyPayments: () => 
    apiClient.get('/Payment/findAll'),
  
  addPayment: (paymentData) => 
    apiClient.post('/Payment/insert', paymentData),
  
  updatePayment: (paymentMethodId, paymentData) => 
    apiClient.put(`/Payment/modify/${paymentMethodId}`, paymentData),
  
  deletePayment: (paymentMethodId) => 
    apiClient.delete(`/Payment/delete/${paymentMethodId}`),
};

// 관리자 API
export const adminAPI = {
  getAllUsers: () => 
    apiClient.get('/Admin/User/findAll'),
  
  getUserById: (userId) => 
    apiClient.get(`/Admin/User/findOne/${userId}`),
  
  deleteUser: (userId) => 
    apiClient.delete(`/Admin/User/delete/${userId}`),
};

export default apiClient;

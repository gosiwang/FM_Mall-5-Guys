import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { isTokenExpired, logout } from './utils/tokenUtils';
import Iridescence from './components/Iridescence';
import Header from './components/Header';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProductDetailPage from './pages/ProductDetailPage';
import CartPage from './pages/CartPage';
import OrderPage from './pages/OrderPage';
import OrderDetailPage from './pages/OrderDetailPage';
import OrderListPage from './pages/OrderListPage';
import MyPage from './pages/MyPage';
import WishListPage from './pages/WishListPage';
import AdminPage from './pages/AdminPage';
import AdminProductPage from './pages/AdminProductPage';
import AdminUserPage from './pages/AdminUserPage';
import './App.css';

function App() {
    // 토큰 만료 확인
    useEffect(() => {
        const checkTokenExpiration = () => {
            const token = localStorage.getItem('token');

            if (token && isTokenExpired(token)) {
                alert('로그인 세션이 만료되었습니다. 다시 로그인해주세요.');
                logout();
            }
        };

        checkTokenExpiration();
        const interval = setInterval(checkTokenExpiration, 60 * 1000);

        return () => clearInterval(interval);
    }, []);

    return (
        <Router>
            <div className="app">

                <Iridescence
                    color={[0.9, 0.95, 1.0]}  // 연한 파란색
                    speed={0.3}                // 느린 애니메이션
                    amplitude={0.1}            // 마우스 반응
                    mouseReact={true}
                    style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        width: '100vw',
                        height: '100vh',
                        zIndex: -1  // 모든 콘텐츠 뒤로
                    }}
                />

                <Header />
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/product/:id" element={<ProductDetailPage />} />
                    <Route path="/cart" element={<CartPage />} />
                    <Route path="/order" element={<OrderPage />} />
                    <Route path="/order/:orderId" element={<OrderDetailPage />} />
                    <Route path="/orders" element={<OrderListPage />} />
                    <Route path="/mypage" element={<MyPage />} />
                    <Route path="/wishlist" element={<WishListPage />} />
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/admin/products" element={<AdminProductPage />} />
                    <Route path="/admin/users" element={<AdminUserPage />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
// App.jsx 수정 - 위시리스트 라우트 추가

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Footer from './components/Footer';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import AdminPage from './pages/AdminPage';
import AdminProductPage from './pages/AdminProductPage';
import AdminUserPage from './pages/AdminUserPage';
import CartPage from "./pages/CartPage";
import MyPage from "./pages/MyPage";
import CartCheckoutPage from './pages/CartCheckoutPage';
import OrderListPage from './pages/OrderListPage';
import OrderDetailPage from './pages/OrderDetailPage';
import WishListPage from './pages/WishListPage'; // ✅ 추가

import './styles.css';

function App() {
    return (
        <Router>
            <div className="app">
                <Header />
                <Routes>
                    {/* 일반 페이지 */}
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignupPage />} />
                    <Route path="/cart" element={<CartPage />} />
                    <Route path="/mypage" element={<MyPage />} />
                    <Route path="/wishlist" element={<WishListPage />} />
                    <Route path="/cart/checkout" element={<CartCheckoutPage />} />
                    <Route path="/orders" element={<OrderListPage />} />
                    <Route path="/orders/:orderId" element={<OrderDetailPage />} />

                    {/* 관리자 페이지 */}
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/admin/products" element={<AdminProductPage />} />
                    <Route path="/admin/users" element={<AdminUserPage />} />
                </Routes>
                <Footer />
            </div>
        </Router>
    );
}

export default App;
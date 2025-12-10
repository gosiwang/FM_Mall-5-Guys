// App.jsx 수정 예제

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


// 필요한 다른 관리자 페이지들도 import

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
                    <Route path="/cart/checkout" element={<CartCheckoutPage />} />
                    <Route path="/orders" element={<OrderListPage />} />
                    <Route path="/orders/:orderId" element={<OrderDetailPage />} />

                    {/* 관리자 페이지 */}

                    <Route path="/admin" element={<AdminPage />} />  {/* 관리자 대시보드 */}

                    <Route path="/admin/products" element={<AdminProductPage />} />  {/* 상품 관리 */}

                    <Route path="/admin/users" element={<AdminUserPage />} />  {/* 사용자 관리 */}

                    {/* 추가 관리자 라우트 */}

                    {/* <Route path="/admin/categories" element={<AdminCategoryPage />} /> */}

                    {/* <Route path="/admin/brands" element={<AdminBrandPage />} /> */}

                    {/* <Route path="/admin/orders" element={<AdminOrderPage />} /> */}

                </Routes>

                <Footer />

            </div>

        </Router>

    );

}

export default App;
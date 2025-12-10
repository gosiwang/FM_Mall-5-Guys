import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderAPI } from '../services/api';

const OrderDetailPage = () => {
    const { orderId } = useParams();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        loadOrderDetail();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [orderId]);

    const loadOrderDetail = async () => {
        try {
            const response = await orderAPI.getOrderDetail(orderId);
            setOrder(response.data);
        } catch (error) {
            console.error('주문 상세 조회 실패:', error);
            alert('주문 상세를 불러오는 데 실패했습니다.');
            navigate('/orders');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <main className="main">
                <aside className="sidebar">
                    <div className="sidebar__section">
                        <div className="sidebar__title">주문 메뉴</div>
                        <ul className="sidebar__list">
                            <li>주문 내역</li>
                            <li>
                                <strong>주문 상세</strong>
                            </li>
                        </ul>
                    </div>
                </aside>
                <section className="content">
                    <div className="order-header">
                        <div>
                            <h1 className="order-header__title">주문 상세</h1>
                            <p className="order-header__subtitle">로딩 중...</p>
                        </div>
                    </div>
                </section>
            </main>
        );
    }

    if (!order) {
        return (
            <main className="main">
                <aside className="sidebar">
                    <div className="sidebar__section">
                        <div className="sidebar__title">주문 메뉴</div>
                        <ul className="sidebar__list">
                            <li>주문 내역</li>
                            <li>
                                <strong>주문 상세</strong>
                            </li>
                        </ul>
                    </div>
                </aside>
                <section className="content">
                    <div className="order-header">
                        <div>
                            <h1 className="order-header__title">주문 상세</h1>
                            <p className="order-header__subtitle">주문 정보를 찾을 수 없습니다.</p>
                        </div>
                    </div>
                </section>
            </main>
        );
    }

    return (
        <main className="main">
            <aside className="sidebar">
                <div className="sidebar__section">
                    <div className="sidebar__title">주문 메뉴</div>
                    <ul className="sidebar__list">
                        <li>
                            <button
                                type="button"
                                className="btn btn--ghost"
                                onClick={() => navigate('/orders')}
                            >
                                주문 내역
                            </button>
                        </li>
                        <li>
                            <strong>주문 상세</strong>
                        </li>
                    </ul>
                </div>
            </aside>

            <section className="content">
                <div className="order-header">
                    <div>
                        <h1 className="order-header__title">주문 상세</h1>
                        <p className="order-header__subtitle">
                            주문번호 #{order.orderId} /{' '}
                            {order.createdAt?.replace('T', ' ').slice(0, 16)}
                        </p>
                    </div>
                    <button
                        type="button"
                        className="btn btn--ghost"
                        onClick={() => navigate('/orders')}
                    >
                        주문 목록으로
                    </button>
                </div>

                {/* 배송지 정보 */}
                <div className="order-section">
                    <div className="order-section__header">
                        <h2 className="order-section__title">배송지 정보</h2>
                    </div>
                    <p className="text-muted">주문 시점의 배송지 정보입니다.</p>
                    <div style={{ marginTop: '0.75rem' }}>
                        <div className="order-info-row">
                            <span className="order-info-label">수령인</span>
                            {order.receiverName} ({order.receiverPhone})
                        </div>
                        <div className="order-info-row">
                            <span className="order-info-label">주소</span>
                            ({order.zipcode}) {order.address1} {order.address2}
                        </div>
                    </div>
                </div>

                {/* 주문 상품 목록 */}
                <div className="order-section">
                    <div className="order-section__header">
                        <h2 className="order-section__title">주문 상품</h2>
                    </div>

                    {order.items && order.items.length > 0 ? (
                        <table className="order-table">
                            <thead>
                            <tr>
                                <th>상품명</th>
                                <th className="text-center">수량</th>
                                <th className="text-right">상품 금액</th>
                                <th className="text-right">합계</th>
                            </tr>
                            </thead>
                            <tbody>
                            {order.items.map((item) => (
                                <tr key={item.orderItemId}>
                                    <td>{item.productName}</td>
                                    <td className="text-center">{item.quantity}</td>
                                    <td className="text-right">
                                        {item.productPrice?.toLocaleString()}원
                                    </td>
                                    <td className="text-right">
                                        {item.lineTotalPrice?.toLocaleString()}원
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    ) : (
                        <p className="text-muted" style={{ marginTop: '0.75rem' }}>
                            주문 상품 정보가 없습니다.
                        </p>
                    )}
                </div>

                {/* 결제 정보 */}
                <div className="order-section">
                    <div className="order-section__header">
                        <h2 className="order-section__title">결제 정보</h2>
                    </div>
                    <p className="text-muted">
                        실제 결제 시스템 연동 전까지는 가상 결제 정보로 처리됩니다.
                    </p>

                    <div style={{ marginTop: '0.75rem' }}>
                        <div className="order-info-row">
                            <span className="order-info-label">총 결제 금액</span>
                            {order.totalPrice?.toLocaleString()}원
                        </div>

                        {order.payment && (
                            <>
                                <div className="order-info-row">
                                    <span className="order-info-label">결제수단</span>
                                    {order.payment.paymentMethodType}
                                </div>
                                {order.payment.approvedAt && (
                                    <div className="text-small text-muted">
                                        결제일시:{' '}
                                        {order.payment.approvedAt.replace('T', ' ').slice(0, 16)}
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>
            </section>
        </main>
    );
};

export default OrderDetailPage;

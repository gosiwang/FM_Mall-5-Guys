import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI, addressAPI, paymentAPI, reviewAPI } from '../services/api';

const MyPage = () => {
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('info');
    const [userInfo, setUserInfo] = useState({});
    const [addresses, setAddresses] = useState([]);
    const [payments, setPayments] = useState([]);
    const [reviews, setReviews] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [editForm, setEditForm] = useState({});
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    // ✅ 배송지 추가 폼
    const [showAddressForm, setShowAddressForm] = useState(false);
    const [addressForm, setAddressForm] = useState({
        receiverName: '',
        receiverPhone: '',
        zipcode: '',
        address1: '',
        address2: '',
        isDefault: 'N', // 백엔드가 String "Y"/"N" 사용
    });

    // ✅ 결제 수단 추가 폼
    const [showPaymentForm, setShowPaymentForm] = useState(false);
    const [paymentForm, setPaymentForm] = useState({
        cardCompany: '',
        maskedCardNumber: '', // 백엔드 DTO 필드명과 맞추기
        isDefault: false,
    });

    // ✅ 리뷰 수정 관련 상태
    const [showReviewEditModal, setShowReviewEditModal] = useState(false);
    const [editingReview, setEditingReview] = useState(null);
    const [reviewEditForm, setReviewEditForm] = useState({
        reviewRating: 5.0,
        reviewContent: ''
    });

    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인이 필요합니다.');
            navigate('/login');
            return;
        }

        loadUserInfo();
        loadAddresses();
        loadPayments();
    }, []);

    useEffect(() => {
        if (activeTab === 'reviews') {
            loadReviews();
        }
    }, [activeTab, currentPage]);

    const loadUserInfo = async () => {
        try {
            setLoading(true);
            const response = await authAPI.getMyInfo();
            setUserInfo(response.data);
            setEditForm(response.data);
        } catch (error) {
            console.error('사용자 정보 로딩 실패:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    const loadAddresses = async () => {
        try {
            const response = await addressAPI.getMyAddresses();
            setAddresses(response.data || []);
        } catch (error) {
            console.error('주소 로딩 실패:', error);
        }
    };

    const loadPayments = async () => {
        try {
            const response = await paymentAPI.getMyPayments();
            setPayments(response.data || []);
        } catch (error) {
            console.error('결제 수단 로딩 실패:', error);
        }
    };

    // ✅ 리뷰 목록 로딩
    const loadReviews = async () => {
        try {
            const response = await reviewAPI.getMyReviews(currentPage);
            setReviews(response.data.content || []);
            setTotalPages(response.data.totalPages || 1);
        } catch (error) {
            console.error('리뷰 로딩 실패:', error);
        }
    };

    // =============================
    // 배송지 추가 폼 관련
    // =============================
    const handleAddressFormChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (name === 'isDefault') {
            setAddressForm((prev) => ({
                ...prev,
                isDefault: checked ? 'Y' : 'N',
            }));
            return;
        }

        setAddressForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmitAddress = async (e) => {
        e.preventDefault();
        try {
            await addressAPI.addAddress(addressForm);
            alert('배송지가 추가되었습니다.');
            setShowAddressForm(false);
            setAddressForm({
                receiverName: '',
                receiverPhone: '',
                zipcode: '',
                address1: '',
                address2: '',
                isDefault: 'N',
            });
            loadAddresses(); // 목록 새로고침
        } catch (error) {
            console.error('배송지 추가 실패:', error);
            alert('배송지 추가에 실패했습니다.');
        }
    };

    // =============================
    // 결제 수단 추가 폼 관련
    // =============================
    const handlePaymentFormChange = (e) => {
        const { name, value, type, checked } = e.target;

        setPaymentForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    const handleSubmitPayment = async (e) => {
        e.preventDefault();
        try {
            await paymentAPI.addPayment(paymentForm);
            alert('결제 수단이 추가되었습니다.');
            setShowPaymentForm(false);
            setPaymentForm({
                cardCompany: '',
                maskedCardNumber: '',
                isDefault: false,
            });
            loadPayments(); // 목록 새로고침
        } catch (error) {
            console.error('결제 수단 추가 실패:', error);
            alert('결제 수단 추가에 실패했습니다.');
        }
    };

    const handleUpdateInfo = async () => {
        try {
            await authAPI.updateUser(editForm);
            alert('정보가 수정되었습니다.');
            setIsEditing(false);
            loadUserInfo();
        } catch (error) {
            console.error('정보 수정 실패:', error);
            alert('정보 수정에 실패했습니다.');
        }
    };

    const handleDeleteAccount = async () => {
        const password = prompt('계정을 삭제하려면 비밀번호를 입력하세요:');
        if (!password) return;

        if (window.confirm('정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
            try {
                await authAPI.deleteUser({ password });
                alert('계정이 삭제되었습니다.');
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                navigate('/');
            } catch (error) {
                console.error('계정 삭제 실패:', error);
                alert('비밀번호를 확인하세요.');
            }
        }
    };

    const handleDeleteAddress = async (addressId) => {
        if (!window.confirm('이 주소를 삭제하시겠습니까?')) return;

        try {
            await addressAPI.deleteAddress(addressId);
            alert('주소가 삭제되었습니다.');
            loadAddresses();
        } catch (error) {
            console.error('주소 삭제 실패:', error);
            alert('주소 삭제에 실패했습니다.');
        }
    };

    const handleDeletePayment = async (paymentMethodId) => {
        if (!window.confirm('이 결제 수단을 삭제하시겠습니까?')) return;

        try {
            await paymentAPI.deletePayment(paymentMethodId);
            alert('결제 수단이 삭제되었습니다.');
            loadPayments();
        } catch (error) {
            console.error('결제 수단 삭제 실패:', error);
            alert('결제 수단 삭제에 실패했습니다.');
        }
    };

    // ✅ 리뷰 수정 모달 열기
    const handleOpenReviewEditModal = (review) => {
        setEditingReview(review);
        setReviewEditForm({
            reviewRating: review.reviewRating,
            reviewContent: review.reviewContent
        });
        setShowReviewEditModal(true);
    };

    // ✅ 리뷰 수정 모달 닫기
    const handleCloseReviewEditModal = () => {
        setShowReviewEditModal(false);
        setEditingReview(null);
        setReviewEditForm({
            reviewRating: 5.0,
            reviewContent: ''
        });
    };

    // ✅ 리뷰 수정 제출
    const handleSubmitReviewEdit = async (e) => {
        e.preventDefault();

        if (!reviewEditForm.reviewContent.trim()) {
            alert('리뷰 내용을 입력해주세요.');
            return;
        }

        try {
            await reviewAPI.updateReview(editingReview.reviewId, reviewEditForm);
            alert('리뷰가 수정되었습니다.');
            handleCloseReviewEditModal();
            loadReviews();
        } catch (error) {
            console.error('리뷰 수정 실패:', error);
            alert('리뷰 수정에 실패했습니다.');
        }
    };

    // ✅ 리뷰 삭제
    const handleDeleteReview = async (reviewId) => {
        if (!window.confirm('이 리뷰를 삭제하시겠습니까?')) return;

        try {
            await reviewAPI.deleteReview(reviewId);
            alert('리뷰가 삭제되었습니다.');
            loadReviews();
        } catch (error) {
            console.error('리뷰 삭제 실패:', error);
            alert('리뷰 삭제에 실패했습니다.');
        }
    };

    if (loading) {
        return (
            <div style={{ textAlign: 'center', padding: '3rem' }}>
                <p>로딩 중...</p>
            </div>
        );
    }

    return (
        <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '1000px' }}>
            <div style={{ backgroundColor: '#ffffff', borderRadius: '1rem', padding: '2rem', border: '1px solid #e5e7eb' }}>
                <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.5rem' }}>
                    마이페이지
                </h1>
                <p style={{ color: '#6b7280', marginBottom: '2rem', fontSize: '0.95rem' }}>
                    내 정보 및 설정 관리
                </p>

                {/* ✅ 탭 메뉴 */}
                <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', borderBottom: '2px solid #e5e7eb', flexWrap: 'wrap' }}>
                    <button
                        onClick={() => setActiveTab('info')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'info' ? '600' : '400',
                            borderBottom: activeTab === 'info' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        내 정보
                    </button>
                    <button
                        onClick={() => setActiveTab('address')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'address' ? '600' : '400',
                            borderBottom: activeTab === 'address' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        배송지
                    </button>
                    <button
                        onClick={() => setActiveTab('payment')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'payment' ? '600' : '400',
                            borderBottom: activeTab === 'payment' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        결제 수단
                    </button>
                    <button
                        onClick={() => navigate('/wishlist')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: '400',
                            color: '#111827'
                        }}
                    >
                        ❤️ 위시리스트
                    </button>
                    <button
                        onClick={() => setActiveTab('reviews')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'reviews' ? '600' : '400',
                            borderBottom: activeTab === 'reviews' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        ⭐ 내 리뷰
                    </button>
                </div>

                {/* 내 정보 탭 */}
                {activeTab === 'info' && (
                    <div>
                        {!isEditing ? (
                            <div>
                                <div style={{ marginBottom: '1.5rem', paddingBottom: '1.5rem', borderBottom: '1px solid #e5e7eb' }}>
                                    <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                        아이디
                                    </label>
                                    <div style={{ fontSize: '1rem' }}>{userInfo.loginId}</div>
                                </div>
                                <div style={{ marginBottom: '1.5rem', paddingBottom: '1.5rem', borderBottom: '1px solid #e5e7eb' }}>
                                    <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                        이름
                                    </label>
                                    <div style={{ fontSize: '1rem' }}>{userInfo.userName}</div>
                                </div>
                                <div style={{ marginBottom: '1.5rem', paddingBottom: '1.5rem', borderBottom: '1px solid #e5e7eb' }}>
                                    <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                        전화번호
                                    </label>
                                    <div style={{ fontSize: '1rem' }}>{userInfo.userPhone || '미등록'}</div>
                                </div>
                                <div style={{ marginBottom: '2rem', paddingBottom: '1.5rem', borderBottom: '1px solid #e5e7eb' }}>
                                    <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                        권한
                                    </label>
                                    <div style={{ fontSize: '1rem' }}>{userInfo.role === 'ADMIN' ? '관리자' : '일반 회원'}</div>
                                </div>
                                <div style={{ display: 'flex', gap: '1rem' }}>
                                    <button onClick={() => setIsEditing(true)} className="btn btn--primary">
                                        정보 수정
                                    </button>
                                    <button onClick={handleDeleteAccount} className="btn btn--outline" style={{ borderColor: '#ef4444', color: '#ef4444' }}>
                                        회원 탈퇴
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div>
                                <div style={{ marginBottom: '1.5rem' }}>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                                        이름
                                    </label>
                                    <input
                                        type="text"
                                        value={editForm.userName || ''}
                                        onChange={(e) => setEditForm({ ...editForm, userName: e.target.value })}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem'
                                        }}
                                    />
                                </div>
                                <div style={{ marginBottom: '1.5rem' }}>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                                        전화번호
                                    </label>
                                    <input
                                        type="tel"
                                        value={editForm.userPhone || ''}
                                        onChange={(e) => setEditForm({ ...editForm, userPhone: e.target.value })}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem'
                                        }}
                                    />
                                </div>
                                <div style={{ display: 'flex', gap: '1rem' }}>
                                    <button onClick={handleUpdateInfo} className="btn btn--primary">
                                        저장
                                    </button>
                                    <button
                                        onClick={() => {
                                            setIsEditing(false);
                                            setEditForm(userInfo);
                                        }}
                                        className="btn btn--ghost"
                                    >
                                        취소
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                )}

                {/* 배송지 관리 탭 */}
                {activeTab === 'address' && (
                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>배송지 목록</h3>
                            <button
                                className="btn btn--primary"
                                onClick={() => setShowAddressForm((prev) => !prev)}
                            >
                                + 배송지 추가
                            </button>
                        </div>

                        {/* ✅ 배송지 추가 폼 */}
                        {showAddressForm && (
                            <form
                                onSubmit={handleSubmitAddress}
                                style={{
                                    marginBottom: '1.5rem',
                                    padding: '1rem',
                                    border: '1px solid #e5e7eb',
                                    borderRadius: '0.5rem',
                                    backgroundColor: '#f9fafb',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: '0.5rem',
                                }}
                            >
                                <div>
                                    <label>받는 사람 이름</label>
                                    <input
                                        name="receiverName"
                                        value={addressForm.receiverName}
                                        onChange={handleAddressFormChange}
                                        className="input"
                                        required
                                    />
                                </div>
                                <div>
                                    <label>전화번호</label>
                                    <input
                                        name="receiverPhone"
                                        value={addressForm.receiverPhone}
                                        onChange={handleAddressFormChange}
                                        className="input"
                                        required
                                    />
                                </div>
                                <div>
                                    <label>우편번호</label>
                                    <input
                                        name="zipcode"
                                        value={addressForm.zipcode}
                                        onChange={handleAddressFormChange}
                                        className="input"
                                        required
                                    />
                                </div>
                                <div>
                                    <label>주소</label>
                                    <input
                                        name="address1"
                                        value={addressForm.address1}
                                        onChange={handleAddressFormChange}
                                        className="input"
                                        required
                                    />
                                </div>
                                <div>
                                    <label>상세 주소</label>
                                    <input
                                        name="address2"
                                        value={addressForm.address2}
                                        onChange={handleAddressFormChange}
                                        className="input"
                                    />
                                </div>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <input
                                        type="checkbox"
                                        name="isDefault"
                                        checked={addressForm.isDefault === 'Y'}
                                        onChange={handleAddressFormChange}
                                    />
                                    기본 배송지로 설정
                                </label>
                                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                                    <button type="submit" className="btn btn--primary">
                                        저장
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn--ghost"
                                        onClick={() => setShowAddressForm(false)}
                                    >
                                        취소
                                    </button>
                                </div>
                            </form>
                        )}

                        {addresses.length === 0 ? (
                            <p style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>
                                등록된 배송지가 없습니다.
                            </p>
                        ) : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                {addresses.map((address) => (
                                    <div
                                        key={address.id}
                                        style={{
                                            padding: '1.25rem',
                                            border: '1px solid #e5e7eb',
                                            borderRadius: '0.5rem',
                                            backgroundColor: '#f9fafb'
                                        }}
                                    >
                                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                                            <div style={{ fontWeight: '500' }}>{address.receiverName}</div>
                                            {address.isDefault === 'Y' && (
                                                <span style={{
                                                    padding: '0.125rem 0.5rem',
                                                    backgroundColor: '#dbeafe',
                                                    color: '#1e40af',
                                                    borderRadius: '0.25rem',
                                                    fontSize: '0.75rem'
                                                }}>
                                                    기본
                                                </span>
                                            )}
                                        </div>
                                        <div style={{ color: '#6b7280', fontSize: '0.875rem', marginBottom: '0.5rem' }}>
                                            [{address.zipcode}] {address.address1} {address.address2}
                                        </div>
                                        <div style={{ color: '#6b7280', fontSize: '0.875rem', marginBottom: '0.75rem' }}>
                                            {address.receiverPhone}
                                        </div>
                                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                                            <button className="btn btn--ghost" style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}>
                                                수정
                                            </button>
                                            <button
                                                onClick={() => handleDeleteAddress(address.id)}
                                                className="btn btn--outline"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem', borderColor: '#ef4444', color: '#ef4444' }}
                                            >
                                                삭제
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* 결제 수단 관리 탭 */}
                {activeTab === 'payment' && (
                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>결제 수단 목록</h3>
                            <button
                                className="btn btn--primary"
                                onClick={() => setShowPaymentForm((prev) => !prev)}
                            >
                                + 결제 수단 추가
                            </button>
                        </div>

                        {/* ✅ 결제 수단 추가 폼 */}
                        {showPaymentForm && (
                            <form
                                onSubmit={handleSubmitPayment}
                                style={{
                                    marginBottom: '1.5rem',
                                    padding: '1rem',
                                    border: '1px solid #e5e7eb',
                                    borderRadius: '0.5rem',
                                    backgroundColor: '#f9fafb',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: '0.5rem',
                                }}
                            >
                                <div>
                                    <label>카드사</label>
                                    <input
                                        name="cardCompany"
                                        value={paymentForm.cardCompany}
                                        onChange={handlePaymentFormChange}
                                        className="input"
                                        required
                                    />
                                </div>
                                <div>
                                    <label>표시용 카드 번호 (예: ****-****-****-1234)</label>
                                    <input
                                        name="maskedCardNumber"
                                        value={paymentForm.maskedCardNumber}
                                        onChange={handlePaymentFormChange}
                                        className="input"
                                        required
                                    />
                                </div>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <input
                                        type="checkbox"
                                        name="isDefault"
                                        checked={paymentForm.isDefault}
                                        onChange={handlePaymentFormChange}
                                    />
                                    기본 결제 수단으로 설정
                                </label>
                                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                                    <button type="submit" className="btn btn--primary">
                                        저장
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn--ghost"
                                        onClick={() => setShowPaymentForm(false)}
                                    >
                                        취소
                                    </button>
                                </div>
                            </form>
                        )}

                        {payments.length === 0 ? (
                            <p style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>
                                등록된 결제 수단이 없습니다.
                            </p>
                        ) : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                {payments.map((payment) => (
                                    <div
                                        key={payment.id}
                                        style={{
                                            padding: '1.25rem',
                                            border: '1px solid #e5e7eb',
                                            borderRadius: '0.5rem',
                                            backgroundColor: '#f9fafb'
                                        }}
                                    >
                                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                                            <div style={{ fontWeight: '500' }}>{payment.cardCompany}</div>
                                            {payment.isDefault && (
                                                <span style={{
                                                    padding: '0.125rem 0.5rem',
                                                    backgroundColor: '#dbeafe',
                                                    color: '#1e40af',
                                                    borderRadius: '0.25rem',
                                                    fontSize: '0.75rem'
                                                }}>
                                                    기본
                                                </span>
                                            )}
                                        </div>
                                        <div style={{ color: '#6b7280', fontSize: '0.875rem', marginBottom: '0.75rem' }}>
                                            {payment.maskedCardNumber}
                                        </div>
                                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                                            <button className="btn btn--ghost" style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}>
                                                수정
                                            </button>
                                            <button
                                                onClick={() => handleDeletePayment(payment.id)}
                                                className="btn btn--outline"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem', borderColor: '#ef4444', color: '#ef4444' }}
                                            >
                                                삭제
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* ✅ 내 리뷰 관리 탭 */}
                {activeTab === 'reviews' && (
                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>내 리뷰</h3>
                            <button onClick={() => navigate('/orders')} className="btn btn--ghost">
                                주문 내역에서 작성
                            </button>
                        </div>
                        {reviews.length === 0 ? (
                            <div style={{ textAlign: 'center', padding: '3rem', backgroundColor: '#f9fafb', borderRadius: '0.75rem' }}>
                                <p style={{ fontSize: '1rem', color: '#6b7280', marginBottom: '1rem' }}>
                                    작성한 리뷰가 없습니다.
                                </p>
                                <button onClick={() => navigate('/orders')} className="btn btn--primary">
                                    주문 내역에서 리뷰 작성하기
                                </button>
                            </div>
                        ) : (
                            <>
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                    {reviews.map((review) => (
                                        <div
                                            key={review.reviewId}
                                            style={{
                                                padding: '1.5rem',
                                                border: '1px solid #e5e7eb',
                                                borderRadius: '0.5rem',
                                                backgroundColor: '#ffffff'
                                            }}
                                        >
                                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
                                                <div>
                                                    <div style={{ fontSize: '1.25rem', marginBottom: '0.25rem' }}>
                                                        {'⭐'.repeat(Math.floor(review.reviewRating))} {review.reviewRating}
                                                    </div>
                                                    <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                                                        {new Date(review.createdAt).toLocaleDateString('ko-KR')}
                                                        {review.updatedAt !== review.createdAt && ' (수정됨)'}
                                                    </div>
                                                </div>
                                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                                    <button
                                                        onClick={() => handleOpenReviewEditModal(review)}
                                                        className="btn btn--ghost"
                                                        style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}
                                                    >
                                                        수정
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteReview(review.reviewId)}
                                                        className="btn btn--outline"
                                                        style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem', borderColor: '#ef4444', color: '#ef4444' }}
                                                    >
                                                        삭제
                                                    </button>
                                                </div>
                                            </div>
                                            <div style={{ fontSize: '0.95rem', lineHeight: '1.6', whiteSpace: 'pre-wrap' }}>
                                                {review.reviewContent}
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                {/* 페이지네이션 */}
                                {totalPages > 1 && (
                                    <div style={{
                                        display: 'flex',
                                        justifyContent: 'center',
                                        gap: '0.5rem',
                                        marginTop: '2rem'
                                    }}>
                                        <button
                                            onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                                            disabled={currentPage === 1}
                                            className="btn btn--outline"
                                            style={{ padding: '0.5rem 1rem' }}
                                        >
                                            이전
                                        </button>
                                        <span style={{ padding: '0.5rem 1rem', display: 'flex', alignItems: 'center' }}>
                                            {currentPage} / {totalPages}
                                        </span>
                                        <button
                                            onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                                            disabled={currentPage === totalPages}
                                            className="btn btn--outline"
                                            style={{ padding: '0.5rem 1rem' }}
                                        >
                                            다음
                                        </button>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                )}

                {/* ✅ 리뷰 수정 모달 */}
                {showReviewEditModal && editingReview && (
                    <div style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.5)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            backgroundColor: '#ffffff',
                            borderRadius: '1rem',
                            padding: '2rem',
                            maxWidth: '500px',
                            width: '90%',
                            maxHeight: '80vh',
                            overflow: 'auto'
                        }}>
                            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', marginBottom: '1.5rem' }}>
                                리뷰 수정
                            </h2>

                            <form onSubmit={handleSubmitReviewEdit}>
                                {/* 평점 */}
                                <div style={{ marginBottom: '1.5rem' }}>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                                        평점
                                    </label>
                                    <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                                        <input
                                            type="range"
                                            min="0.5"
                                            max="5.0"
                                            step="0.5"
                                            value={reviewEditForm.reviewRating}
                                            onChange={(e) => setReviewEditForm({
                                                ...reviewEditForm,
                                                reviewRating: parseFloat(e.target.value)
                                            })}
                                            style={{ flex: 1 }}
                                        />
                                        <span style={{ fontSize: '1.25rem', fontWeight: '600', minWidth: '3rem' }}>
                                            {'⭐'.repeat(Math.floor(reviewEditForm.reviewRating))} {reviewEditForm.reviewRating}
                                        </span>
                                    </div>
                                </div>

                                {/* 리뷰 내용 */}
                                <div style={{ marginBottom: '1.5rem' }}>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                                        리뷰 내용
                                    </label>
                                    <textarea
                                        value={reviewEditForm.reviewContent}
                                        onChange={(e) => setReviewEditForm({ ...reviewEditForm, reviewContent: e.target.value })}
                                        placeholder="상품에 대한 솔직한 리뷰를 작성해주세요."
                                        required
                                        rows="6"
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            resize: 'vertical'
                                        }}
                                    />
                                </div>

                                {/* 버튼 */}
                                <div style={{ display: 'flex', gap: '1rem' }}>
                                    <button
                                        type="submit"
                                        className="btn btn--primary"
                                        style={{ flex: 1 }}
                                    >
                                        수정 완료
                                    </button>
                                    <button
                                        type="button"
                                        onClick={handleCloseReviewEditModal}
                                        className="btn btn--ghost"
                                        style={{ flex: 1 }}
                                    >
                                        취소
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        </main>
    );
};

export default MyPage;

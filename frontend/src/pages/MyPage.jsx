import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI, addressAPI, paymentAPI } from '../services/api';

const MyPage = () => {
    const [activeTab, setActiveTab] = useState('info');
    const [userInfo, setUserInfo] = useState(null);
    const [addresses, setAddresses] = useState([]);
    const [payments, setPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [editForm, setEditForm] = useState({});

    const navigate = useNavigate();

    useEffect(() => {
        loadUserInfo();
        loadAddresses();
        loadPayments();
    }, []);

    const loadUserInfo = async () => {
        try {
            const response = await authAPI.getMyInfo();
            setUserInfo(response.data);
            setEditForm(response.data);
        } catch (error) {
            console.error('사용자 정보 로딩 실패:', error);
        } finally {
            setLoading(false);
        }
    };

    const loadAddresses = async () => {
        try {
            const response = await addressAPI.getMyAddresses();
            setAddresses(response.data || []);
        } catch (error) {
            console.error('주소 목록 로딩 실패:', error);
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

    const handleUpdateInfo = async () => {
        try {
            await authAPI.updateUser({
                userName: editForm.userName,
                userPhone: editForm.userPhone
            });
            alert('정보가 수정되었습니다.');
            setIsEditing(false);
            loadUserInfo();
        } catch (error) {
            console.error('정보 수정 실패:', error);
            alert('정보 수정에 실패했습니다.');
        }
    };

    const handleDeleteAccount = async () => {
        const password = prompt('회원 탈퇴를 위해 비밀번호를 입력하세요:');
        if (!password) return;

        if (!window.confirm('정말로 탈퇴하시겠습니까?')) return;

        try {
            await authAPI.deleteUser({ password });
            alert('회원 탈퇴가 완료되었습니다.');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            navigate('/');
        } catch (error) {
            console.error('회원 탈퇴 실패:', error);
            alert('회원 탈퇴에 실패했습니다. 비밀번호를 확인하세요.');
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

                {/* 탭 메뉴 */}
                <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', borderBottom: '2px solid #e5e7eb' }}>
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
                        배송지 관리
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
                </div>

                {/* 내 정보 탭 */}
                {activeTab === 'info' && (
                    <div>
                        {!isEditing ? (
                            <div>
                                <div style={{ marginBottom: '1rem' }}>
                                    <strong>로그인 ID:</strong> {userInfo?.loginId}
                                </div>
                                <div style={{ marginBottom: '1rem' }}>
                                    <strong>이름:</strong> {userInfo?.userName}
                                </div>
                                <div style={{ marginBottom: '1.5rem' }}>
                                    <strong>전화번호:</strong> {userInfo?.userPhone || '-'}
                                </div>
                                <div style={{ display: 'flex', gap: '1rem' }}>
                                    <button
                                        onClick={() => setIsEditing(true)}
                                        className="btn btn--primary"
                                    >
                                        정보 수정
                                    </button>
                                    <button
                                        onClick={handleDeleteAccount}
                                        className="btn btn--outline"
                                        style={{ borderColor: '#ef4444', color: '#ef4444' }}
                                    >
                                        회원 탈퇴
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div>
                                <div style={{ marginBottom: '1rem' }}>
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
                            <button className="btn btn--primary">+ 배송지 추가</button>
                        </div>
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
                                            border: '1px solid #e5e7eb',
                                            borderRadius: '0.5rem',
                                            padding: '1rem'
                                        }}
                                    >
                                        <div style={{ marginBottom: '0.5rem' }}>
                                            <strong>{address.addressName || '배송지'}</strong>
                                            {address.isDefault === 'Y' && (
                                                <span style={{
                                                    marginLeft: '0.5rem',
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
                                            {address.address1} {address.address2}
                                        </div>
                                        <div style={{ color: '#6b7280', fontSize: '0.875rem', marginBottom: '0.75rem' }}>
                                            {address.receiverName} | {address.receiverPhone}
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

                {/* 결제 수단 탭 */}
                {activeTab === 'payment' && (
                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>결제 수단 목록</h3>
                            <button className="btn btn--primary">+ 결제 수단 추가</button>
                        </div>
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
                                            border: '1px solid #e5e7eb',
                                            borderRadius: '0.5rem',
                                            padding: '1rem'
                                        }}
                                    >
                                        <div style={{ marginBottom: '0.5rem' }}>
                                            <strong>{payment.cardCompany || '카드'}</strong>
                                            {payment.isDefault === 'Y' && (
                                                <span style={{
                                                    marginLeft: '0.5rem',
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
                                            **** **** **** {payment.cardNumber?.slice(-4) || '****'}
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
            </div>
        </main>
    );
};

export default MyPage;
import React, { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';

const AdminPage = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedUser, setSelectedUser] = useState(null);

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            setLoading(true);
            const response = await adminAPI.getAllUsers();
            setUsers(response.data || []);
        } catch (error) {
            console.error('사용자 목록 로딩 실패:', error);
            alert('사용자 목록을 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteUser = async (userId, loginId) => {
        if (!window.confirm(`${loginId} 사용자를 삭제하시겠습니까?`)) {
            return;
        }

        try {
            await adminAPI.deleteUser(userId);
            alert('사용자가 삭제되었습니다.');
            loadUsers();
        } catch (error) {
            console.error('사용자 삭제 실패:', error);
            alert('사용자 삭제에 실패했습니다.');
        }
    };

    const handleViewDetail = async (userId) => {
        try {
            const response = await adminAPI.getUserById(userId);
            setSelectedUser(response.data);
        } catch (error) {
            console.error('사용자 상세 정보 로딩 실패:', error);
            alert('사용자 정보를 불러오는데 실패했습니다.');
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
        <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '1200px' }}>
            <div style={{ backgroundColor: '#ffffff', borderRadius: '1rem', padding: '2rem', border: '1px solid #e5e7eb' }}>
                <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.5rem' }}>
                    관리자 페이지
                </h1>
                <p style={{ color: '#6b7280', marginBottom: '2rem', fontSize: '0.95rem' }}>
                    시스템 사용자 관리
                </p>

                {/* 사용자 목록 테이블 */}
                <div style={{ overflowX: 'auto' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                        <tr style={{ borderBottom: '2px solid #e5e7eb' }}>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>ID</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>로그인 ID</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>이름</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>전화번호</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>권한</th>
                            <th style={{ padding: '1rem', textAlign: 'center', fontWeight: '600' }}>관리</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map((user) => (
                            <tr key={user.id} style={{ borderBottom: '1px solid #f3f4f6' }}>
                                <td style={{ padding: '1rem' }}>{user.id}</td>
                                <td style={{ padding: '1rem' }}>{user.loginId}</td>
                                <td style={{ padding: '1rem' }}>{user.userName}</td>
                                <td style={{ padding: '1rem' }}>{user.userPhone || '-'}</td>
                                <td style={{ padding: '1rem' }}>
                    <span style={{
                        padding: '0.25rem 0.5rem',
                        borderRadius: '0.375rem',
                        fontSize: '0.875rem',
                        backgroundColor: user.role === 'ADMIN' ? '#fee2e2' : '#dbeafe',
                        color: user.role === 'ADMIN' ? '#b91c1c' : '#1e40af'
                    }}>
                      {user.role}
                    </span>
                                </td>
                                <td style={{ padding: '1rem', textAlign: 'center' }}>
                                    <button
                                        onClick={() => handleViewDetail(user.id)}
                                        className="btn btn--ghost"
                                        style={{ marginRight: '0.5rem', padding: '0.375rem 0.75rem', fontSize: '0.875rem' }}
                                    >
                                        상세
                                    </button>
                                    <button
                                        onClick={() => handleDeleteUser(user.id, user.loginId)}
                                        className="btn btn--outline"
                                        style={{ padding: '0.375rem 0.75rem', fontSize: '0.875rem', borderColor: '#ef4444', color: '#ef4444' }}
                                    >
                                        삭제
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>

                {/* 사용자 상세 정보 모달 */}
                {selectedUser && (
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
                            width: '90%'
                        }}>
                            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', marginBottom: '1.5rem' }}>
                                사용자 상세 정보
                            </h2>
                            <div style={{ marginBottom: '1rem' }}>
                                <strong>로그인 ID:</strong> {selectedUser.loginId}
                            </div>
                            <div style={{ marginBottom: '1rem' }}>
                                <strong>이름:</strong> {selectedUser.userName}
                            </div>
                            <div style={{ marginBottom: '1rem' }}>
                                <strong>전화번호:</strong> {selectedUser.userPhone || '-'}
                            </div>
                            <div style={{ marginBottom: '1.5rem' }}>
                                <strong>권한:</strong> {selectedUser.role}
                            </div>
                            <button
                                onClick={() => setSelectedUser(null)}
                                className="btn btn--primary full-width"
                            >
                                닫기
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </main>
    );
};

export default AdminPage;
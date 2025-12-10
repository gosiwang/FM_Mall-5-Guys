import React from 'react';
import { useNavigate } from 'react-router-dom';
import { cartAPI } from '../services/api';

const ProductCard = ({ product }) => {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/product/${product.productId}`);
    };

    const handleAddToCart = async (e) => {
        e.stopPropagation();

        // 로그인 확인
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인이 필요합니다.');
            navigate('/login');
            return;
        }

        try {
            // 장바구니에 상품 추가 API 호출
            await cartAPI.addToCart({
                productId: product.productId,
                quantity: 1
            });

            alert('장바구니에 추가되었습니다.');

            // 장바구니로 이동할지 물어보기
            if (window.confirm('장바구니로 이동하시겠습니까?')) {
                navigate('/cart');
            }
        } catch (error) {
            console.error('장바구니 추가 실패:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            } else if (error.response?.status === 404) {
                alert('상품을 찾을 수 없습니다.');
            } else {
                alert('장바구니 추가에 실패했습니다.');
            }
        }
    };

    return (
        <article className="product-card" onClick={handleCardClick} style={{ cursor: 'pointer' }}>
            {product.isNew && (
                <div className="product-card__badge">NEW</div>
            )}
            {product.isHot && (
                <div className="product-card__badge product-card__badge--green">HOT</div>
            )}

            <div className="product-card__image">
                {product.imageUrl ? (
                    <img
                        src={product.imageUrl}
                        alt={product.productName}
                        style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '0.75rem' }}
                    />
                ) : (
                    <span>{product.categoryName || '상품'}</span>
                )}
            </div>

            <h3 className="product-card__name">{product.productName}</h3>
            <p className="product-card__brand">{product.brandName || '브랜드'}</p>

            <p className="product-card__price">
                {product.productPrice?.toLocaleString('ko-KR')}원
            </p>

            <p className="product-card__desc">
                {product.description || '상품 설명이 없습니다.'}
            </p>

            <button
                className="btn btn--outline full-width"
                onClick={handleAddToCart}
            >
                장바구니 담기
            </button>
        </article>
    );
};

export default ProductCard;
import React from 'react';
import { useNavigate } from 'react-router-dom';

const ProductCard = ({ product }) => {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/product/${product.productId}`);
    };

    const handleAddToCart = (e) => {
        e.stopPropagation();
        alert('장바구니에 추가되었습니다.');
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

            {/* ✅ price → productPrice로 수정 */}
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
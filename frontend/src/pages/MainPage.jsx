import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import ProductCard from '../components/ProductCard';
import IntroStack from '../components/IntroStack';
import { productAPI } from '../services/api';
import { shouldShowIntro, markIntroAsViewed } from '../utils/introUtils'; // ✅ 추가

const MainPage = () => {
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [sortOption, setSortOption] = useState('recommended');
    const [loading, setLoading] = useState(true);
    const [showIntro, setShowIntro] = useState(true);
    const [introComplete, setIntroComplete] = useState(false);

    useEffect(() => {

        if (!shouldShowIntro()) {
            setShowIntro(false);
            setIntroComplete(true);
        }

        loadProducts();
    }, []);

    const loadProducts = async () => {
        try {
            setLoading(true);
            const response = await productAPI.getAllProducts();
            const productData = response.data || [];
            setProducts(productData);
            setFilteredProducts(productData);
        } catch (error) {
            console.error('상품 로딩 실패:', error);
        } finally {
            setLoading(false);
            setTimeout(() => {
                handleIntroComplete();
            }, 7000);
        }
    };

    const handleIntroComplete = () => {
        markIntroAsViewed();
        setShowIntro(false);
        setTimeout(() => setIntroComplete(true), 500);
    };


    const handleFilterChange = (filters) => {
        let filtered = [...products];

        // 1) 카테고리
        if (filters.categoryId !== null && filters.categoryId !== undefined) {
            filtered = filtered.filter(p => p.categoryId === filters.categoryId);
        }

        // 2) 브랜드 (Sidebar 에서 넘어오는 brands: ['삼성', 'LG', ...])
        if (filters.brandIds && filters.brandIds.length > 0) {
            filtered = filtered.filter(p =>
                filters.brandIds.includes(p.brandId)   // ✅ ProductResponseDTO.brandId와 비교
            );
        }

        // 3) 가격대 (priceRange: { min: '', max: '' })
        if (filters.priceRange) {
            const min =
                filters.priceRange.min !== ''
                    ? Number(filters.priceRange.min)
                    : null;
            const max =
                filters.priceRange.max !== ''
                    ? Number(filters.priceRange.max)
                    : null;

            if (min !== null && !Number.isNaN(min)) {
                filtered = filtered.filter(p => p.productPrice >= min);
            }

            if (max !== null && !Number.isNaN(max)) {
                filtered = filtered.filter(p => p.productPrice <= max);
            }
        }

        setFilteredProducts(filtered);
    };

    const handleSortChange = (sortValue) => {
        setSortOption(sortValue);
        let sorted = [...filteredProducts];

        switch (sortValue) {
            case 'price-low':
                sorted.sort((a, b) => a.productPrice - b.productPrice);
                break;
            case 'price-high':
                sorted.sort((a, b) => b.productPrice - a.productPrice);
                break;
            case 'newest':
                sorted.sort((a, b) => b.productId - a.productId);
                break;
            default:
                break;
        }

        setFilteredProducts(sorted);
    };

    // 인트로 화면
    if (!introComplete) {
        return (
            <div style={{
                opacity: showIntro ? 1 : 0,
                transition: 'opacity 0.5s ease-out'
            }}>
                <IntroStack onComplete={handleIntroComplete} />
            </div>
        );
    }

    // 메인 콘텐츠
    return (
        <main className="main">
            <Sidebar onFilterChange={handleFilterChange} />

            <section className="content">
                <div className="toolbar">
                    <div className="toolbar__left">
                        <h1 className="toolbar__title">전체 상품</h1>
                        <p className="toolbar__subtitle">{filteredProducts.length}개의 상품</p>
                    </div>

                    <select
                        className="select"
                        value={sortOption}
                        onChange={(e) => handleSortChange(e.target.value)}
                    >
                        <option value="recommended">추천순</option>
                        <option value="newest">신상품순</option>
                        <option value="price-low">낮은 가격순</option>
                        <option value="price-high">높은 가격순</option>
                    </select>
                </div>

                <section className="product-grid">
                    {loading ? (
                        <div style={{
                            gridColumn: '1 / -1',
                            textAlign: 'center',
                            padding: '3rem',
                            color: '#6b7280'
                        }}>
                            <p>상품을 불러오는 중...</p>
                        </div>
                    ) : filteredProducts.length > 0 ? (
                        filteredProducts.map((product) => (
                            <ProductCard key={product.productId} product={product} />
                        ))
                    ) : (
                        <div style={{
                            gridColumn: '1 / -1',
                            textAlign: 'center',
                            padding: '3rem',
                            color: '#6b7280'
                        }}>
                            <p>해당 조건의 상품이 없습니다.</p>
                        </div>
                    )}
                </section>
            </section>
        </main>
    );
};

export default MainPage;

import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import ProductCard from '../components/ProductCard';
import { productAPI } from '../services/api';

const MainPage = () => {
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [sortOption, setSortOption] = useState('recommended');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
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
      // 임시 데이터 설정
      const mockProducts = [
        {
          productId: 1,
          productName: '75인치 4K 스마트 TV',
          brandName: '삼성전자',
          price: 1590000,
          description: '넷플릭스, 유튜브 기본 탑재 / 슬림 베젤 디자인',
          isNew: true,
          categoryName: 'TV'
        },
        {
          productId: 2,
          productName: '870L 프리미엄 양문형 냉장고',
          brandName: 'LG전자',
          price: 2190000,
          description: '인버터 컴프레서 / 에너지 효율 1등급',
          isHot: true,
          categoryName: '냉장고'
        },
        {
          productId: 3,
          productName: '21kg 드럼 세탁기 + 건조기 세트',
          brandName: '삼성전자',
          price: 2590000,
          description: 'AI 추천 코스 / 살균 건조 / 저소음 설계',
          categoryName: '세탁기'
        },
        {
          productId: 4,
          productName: '무선 스틱 청소기',
          brandName: '다이슨',
          price: 890000,
          description: '최대 60분 사용 / 초경량 바디',
          categoryName: '청소기'
        }
      ];
      setProducts(mockProducts);
      setFilteredProducts(mockProducts);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (filters) => {
    let filtered = [...products];

    // 카테고리 필터
    if (filters.categoryId) {
      filtered = filtered.filter(p => p.categoryId === filters.categoryId);
    }

  /*  // 브랜드 필터
    if (filters.brands && filters.brands.length > 0) {
      filtered = filtered.filter(p => 
        filters.brands.some(brand => 
          p.brandName?.includes(brand) || brand === '기타'
        )
      );
    }*/

    // 가격 필터
    if (filters.priceRange) {
      const { min, max } = filters.priceRange;
      if (min) {
        filtered = filtered.filter(p => p.price >= parseInt(min));
      }
      if (max) {
        filtered = filtered.filter(p => p.price <= parseInt(max));
      }
    }

    setFilteredProducts(filtered);
  };

  const handleSortChange = (e) => {
    const option = e.target.value;
    setSortOption(option);

    let sorted = [...filteredProducts];
    switch (option) {
      case 'priceAsc':
        sorted.sort((a, b) => a.price - b.price);
        break;
      case 'priceDesc':
        sorted.sort((a, b) => b.price - a.price);
        break;
      case 'new':
        sorted.sort((a, b) => (b.isNew ? 1 : 0) - (a.isNew ? 1 : 0));
        break;
      default:
        // 추천순 (기본)
        break;
    }
    setFilteredProducts(sorted);
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '3rem' }}>
        <p>로딩 중...</p>
      </div>
    );
  }

  return (
    <main className="main">
      <Sidebar onFilterChange={handleFilterChange} />

      <section className="content">
        {/* 히어로 배너 */}
        <section className="hero">
          <div className="hero__text">
            <h1>연말 특가 전자제품 세일</h1>
            <p>
              TV, 냉장고, 세탁기, 생활가전까지<br />
              모든 전자제품을 한 번에 비교하고 합리적인 가격에 구매하세요.
            </p>
            <div className="hero__actions">
              <button className="btn btn--primary">오늘의 특가 보기</button>
              <button className="btn btn--ghost">카테고리 둘러보기</button>
            </div>
          </div>
          <div className="hero__image">
            <div className="hero__mockup">
              <span>제품 모음 이미지 영역</span>
            </div>
          </div>
        </section>

        {/* 정렬 영역 */}
        <section className="toolbar">
          <div className="toolbar__left">
            <h2 className="toolbar__title">추천 상품</h2>
            <span className="toolbar__subtitle">총 {filteredProducts.length}개 상품</span>
          </div>
          <div className="toolbar__right">
            <select 
              className="select" 
              value={sortOption}
              onChange={handleSortChange}
            >
              <option value="recommended">추천순</option>
              <option value="priceAsc">낮은 가격순</option>
              <option value="priceDesc">높은 가격순</option>
              <option value="new">신상품순</option>
            </select>
          </div>
        </section>

        {/* 상품 카드 리스트 */}
        <section className="product-grid">
          {filteredProducts.length > 0 ? (
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

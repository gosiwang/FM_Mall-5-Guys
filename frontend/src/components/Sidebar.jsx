import React, { useState, useEffect } from 'react';
import { categoryAPI } from '../services/api';

const Sidebar = ({ onFilterChange }) => {
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedBrands, setSelectedBrands] = useState([]);
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });

  const brands = ['삼성', 'LG', '다이슨', '기타'];

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const response = await categoryAPI.getAllCategories();
      setCategories(response.data || []);
    } catch (error) {
      console.error('카테고리 로딩 실패:', error);
      // 기본 카테고리 설정
      setCategories([
        { columnCategoryId: 1, columnCategoryName: 'TV / 영상가전' },
        { columnCategoryId: 2, columnCategoryName: '세탁기 / 건조기' },
        { columnCategoryId: 3, columnCategoryName: '냉장고' },
        { columnCategoryId: 4, columnCategoryName: '청소기 / 생활가전' },
        { columnCategoryId: 5, columnCategoryName: '에어컨 / 공기청정기' },
      ]);
    }
  };

  const handleCategoryClick = (categoryId) => {
    setSelectedCategory(categoryId);
    applyFilters({ categoryId, brands: selectedBrands, priceRange });
  };

  const handleBrandChange = (brand) => {
    const updatedBrands = selectedBrands.includes(brand)
      ? selectedBrands.filter(b => b !== brand)
      : [...selectedBrands, brand];
    
    setSelectedBrands(updatedBrands);
  };

  const applyFilters = (filters = null) => {
    const filterData = filters || {
      categoryId: selectedCategory,
      brands: selectedBrands,
      priceRange
    };
    
    if (onFilterChange) {
      onFilterChange(filterData);
    }
  };

  return (
    <aside className="sidebar">
      <section className="sidebar__section">
        <h2 className="sidebar__title">카테고리</h2>
        <ul className="sidebar__list">
          <li>
            <a 
              href="#" 
              onClick={(e) => {
                e.preventDefault();
                handleCategoryClick(null);
              }}
              style={{ 
                fontWeight: selectedCategory === null ? 'bold' : 'normal',
                color: selectedCategory === null ? '#111827' : '#4b5563'
              }}
            >
              전체
            </a>
          </li>
          {categories.map((category) => (
            <li key={category.columnCategoryId}>
              <a 
                href="#" 
                onClick={(e) => {
                  e.preventDefault();
                  handleCategoryClick(category.columnCategoryId);
                }}
                style={{ 
                  fontWeight: selectedCategory === category.columnCategoryId ? 'bold' : 'normal',
                  color: selectedCategory === category.columnCategoryId ? '#111827' : '#4b5563'
                }}
              >
                {category.columnCategoryName}
              </a>
            </li>
          ))}
        </ul>
      </section>

      <section className="sidebar__section">
        <h2 className="sidebar__title">브랜드</h2>
        {brands.map((brand) => (
          <label key={brand} className="sidebar__checkbox">
            <input 
              type="checkbox" 
              checked={selectedBrands.includes(brand)}
              onChange={() => handleBrandChange(brand)}
            />
            {brand}
          </label>
        ))}
      </section>

      <section className="sidebar__section">
        <h2 className="sidebar__title">가격대</h2>
        <div className="price-range">
          <input 
            type="number" 
            placeholder="최소"
            value={priceRange.min}
            onChange={(e) => setPriceRange({ ...priceRange, min: e.target.value })}
          />
          <span>~</span>
          <input 
            type="number" 
            placeholder="최대"
            value={priceRange.max}
            onChange={(e) => setPriceRange({ ...priceRange, max: e.target.value })}
          />
        </div>
        <button 
          className="btn btn--outline full-width"
          onClick={() => applyFilters()}
        >
          필터 적용
        </button>
      </section>
    </aside>
  );
};

export default Sidebar;

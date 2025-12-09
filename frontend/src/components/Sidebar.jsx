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
            console.log('카테고리 데이터:', response.data); // ✅ 디버깅용
            setCategories(response.data || []);
        } catch (error) {
            console.error('카테고리 로딩 실패:', error);
            setCategories([]);
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
                    <li key="all">
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
                        <li key={category.categoryId}>
                            <a
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault();
                                    handleCategoryClick(category.categoryId);
                                }}
                                style={{
                                    fontWeight: selectedCategory === category.categoryId ? 'bold' : 'normal',
                                    color: selectedCategory === category.categoryId ? '#111827' : '#4b5563'
                                }}
                            >
                                {/* ✅ categoryName으로 수정 */}
                                {category.categoryName}
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
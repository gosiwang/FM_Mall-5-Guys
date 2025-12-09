package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.CategoryDTO;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    // SecurityConfig 때문에 필요한 MockBean 들 (ProductServiceTest 와 동일 패턴)
    @MockBean
    private PasswordEncoder passwordEncoder;

    // Service 에서 사용하는 ModelMapper 도 MockBean 으로 등록
    @MockBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("insertCategory - 상위 카테고리 등록 시 DB에 저장되고 DTO가 반환된다.")
    void insertCategory_success() {
        // 🔹 1) 요청 DTO 준비
        CategoryDTO requestDTO = CategoryDTO.builder()
                .categoryName("테스트 상위 카테고리")
                .build();

        // 🔹 2) ModelMapper mock 이 실제 매핑을 하도록 설정
        given(modelMapper.map(any(Category.class), eq(CategoryDTO.class)))
                .willAnswer(invocation -> {
                    Category source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    // Category.name -> CategoryDTO.categoryName 매핑
                    real.typeMap(Category.class, CategoryDTO.class)
                            .addMappings(m -> {
                                m.map(Category::getCategoryId, CategoryDTO::setCategoryId);
                                m.map(Category::getName, CategoryDTO::setCategoryName);
                            });

                    return real.map(source, CategoryDTO.class);
                });

        // 🔹 3) 서비스 호출
        CategoryDTO result = categoryService.insertCategory(requestDTO);

        System.out.println("=== 🔥 DTO로 반환된 결과 ===");
        System.out.println(result.getCategoryId() + " / " + result.getCategoryName());

        // 🔹 4) DTO 검증
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId())
                .as("DB에 저장되면서 category_id 가 생성되어야 합니다.")
                .isGreaterThan(0);
        assertThat(result.getCategoryName()).isEqualTo("테스트 상위 카테고리");

        // 🔹 5) 실제 DB에 제대로 들어갔는지 검증
        Optional<Category> optionalCategory = categoryRepository.findById(result.getCategoryId());
        assertThat(optionalCategory)
                .as("반환된 categoryId 로 DB에서 조회가 되어야 합니다.")
                .isPresent();

        Category saved = optionalCategory.get();

        System.out.println("=== 🔥 DB에서 다시 읽어온 Entity ===");
        System.out.println(saved.getCategoryId() + " / " + saved.getName());

        assertThat(saved.getName()).isEqualTo("테스트 상위 카테고리");
    }

    @Test
    @DisplayName("modifyCategory - 기존 상위 카테고리 수정 시 변경 내용이 반영된다.")
    void modifyCategory_success() {
        // 🔹 1) 수정 대상 카테고리 하나 선택 (DB에 최소 1개 있다고 가정)
        List<Category> all = categoryRepository.findAll();
        assertThat(all)
                .as("수정 테스트를 위해 최소 1개 이상의 상위 카테고리 데이터가 필요합니다.")
                .isNotEmpty();

        Category original = all.get(3);
        int categoryId = original.getCategoryId();

        String updatedName = original.getName() + "_수정";

        // 🔹 2) 요청 DTO 생성
        CategoryDTO requestDTO = CategoryDTO.builder()
                .categoryName(updatedName)
                .build();

        // 🔹 3) ModelMapper mock → 실제 매핑 수행하도록 설정
        given(modelMapper.map(any(Category.class), eq(CategoryDTO.class)))
                .willAnswer(invocation -> {
                    Category source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    real.typeMap(Category.class, CategoryDTO.class)
                            .addMappings(m -> {
                                m.map(Category::getCategoryId, CategoryDTO::setCategoryId);
                                m.map(Category::getName, CategoryDTO::setCategoryName);
                            });

                    return real.map(source, CategoryDTO.class);
                });

        // 🔹 4) 서비스 호출
        CategoryDTO result = categoryService.modifyCategory(categoryId, requestDTO);

        // 🔹 5) DTO 검증
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
        assertThat(result.getCategoryName()).isEqualTo(updatedName);

        // 🔹 6) 실제 DB에 반영됐는지 확인
        Category updated = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AssertionError("수정된 카테고리가 DB에 존재하지 않습니다."));

        assertThat(updated.getName()).isEqualTo(updatedName);

        // 🔹 7) updated_at 이 DB에서 잘 갱신되는지(DDL 설정이 되어 있다면) 확인하고 싶다면:
        // assertThat(updated.getUpdatedAt()).isNotNull();
    }
}

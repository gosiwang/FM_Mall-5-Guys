package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.CategoryDTO;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    /* 추가할 상위 카테고리를 입력 후 , 상위 카테고리를 추가/등록 */
    @Transactional
    public CategoryDTO insertCategory(CategoryDTO categoryDTO) {

        // DTO -> Entity 변환.
        Category newCategory = Category.builder()
                .name(categoryDTO.getCategoryName())
                .build();

        // 내부적으로 EntityManager.persist( ) 호출되어 영속성 컨텍스트로 들어간다.
        Category savedCategory = categoryRepository.save(newCategory);

        // 저장 후 생성된 Entity를 다시 DTO로 변환하여 반환.
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /* categoryId값을 넘겨, 해당 상위 카테고리 정보 수정 진행. */
    @Transactional
    public CategoryDTO modifyCategory(int categoryId, CategoryDTO categoryDTO) {
        Category foundCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 카테고리입니다."));

        foundCategory.modify(
                categoryDTO.getCategoryName()
        );

        return modelMapper.map(foundCategory, CategoryDTO.class);
    }


    /* categoryId값을 넘겨, 해당 상위 카테고리 정보 삭제 진행 */

    /* categoryId값을 넘겨 해당 상위카테고리의 전체 상품 목록 조회. */
}

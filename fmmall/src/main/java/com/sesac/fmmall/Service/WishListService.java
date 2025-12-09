package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.WishList.WishListRequestDTO;
import com.sesac.fmmall.DTO.WishList.WishListResponseDTO;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.WishList;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.WishListRepository;
import com.sesac.fmmall.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /* 1. 위시리스트 코드로 상세 조회 */
    public WishListResponseDTO findWishListByWishListId(int wishListId) {
        WishList foundWishList = wishListRepository.findById(wishListId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID를 가진 위시리스트가 존재하지 않습니다."));

//        return new WishListResponseDTO(foundWishList);
        return WishListResponseDTO.from(foundWishList);
    }

    /* 2. 유저별 위시리스트 생성순 상세 조회 */
    public Page<WishListResponseDTO> findWishListByUserIdSortedCreatedAt(int userId, int curPage) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 2. 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신 생성순 정렬)
        int page = curPage <= 0 ? 0 : curPage - 1;
        int size = 20;   // 위시리스트는 한 페이지에 20개씩만
        String sortDir = "createdAt";

        // Sort.by(sortDir).descending() -> 최신 생성순(내림차순)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 3. 리포지토리 호출 (유저 ID로 필터링 + 페이징/정렬 적용)
        Page<WishList> wishListList = wishListRepository.findAllByUser_UserId(userId, pageRequest);

        // 4. Entity -> DTO 변환 후 반환
        return wishListList.map(WishListResponseDTO::from);
    }

    /* 3. 위시리스트 등록 */
    @Transactional
    public WishListResponseDTO insertWishList(WishListRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // DTO -> Entity 변환 (builder 패턴 사용)
        WishList newWishList = WishList.builder()
                .user(user)
                .product(product)
                .build();

        // 내부적으로 EntityManager.persist() 호출되어 영속성 컨텍스트로 들어간다.
        WishList savedWishList = wishListRepository.save(newWishList);

        // 저장 후, 생성된 Entity를 다시 DTO로 변환하여 반환
        return WishListResponseDTO.from(savedWishList);
//        return modelMapper.map(savedWishList, WishListResponseDTO.class);
//        return new WishListResponseDTO(savedWishList);
    }

    /* 4. 위시리스트 삭제 */
    @Transactional
    public void deleteWishList(int wishListId) {

        if (!wishListRepository.existsById(wishListId)) {
            throw new IllegalArgumentException("삭제할 위시리스트가 존재하지 않습니다.");

        }

        wishListRepository.deleteById(wishListId);
    }

    /* 5. 위시리스트 토클 형식. 사실상 삽입, 삭제를 담당하기에 위에 것들은 필요없음. */
    @Transactional
    public WishListResponseDTO toggleWishlist(WishListRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Optional<Integer> wishListItem =
                wishListRepository.findIdByUserIdAndProductId(
                        requestDTO.getUserId(), requestDTO.getProductId()
                );

        if (wishListItem.isPresent()) {
            wishListRepository.deleteById(wishListItem.get());
            return WishListResponseDTO.removedDTO(); // 삭제
        } else {
            WishList newWishList = WishList.builder()
                    .user(user)
                    .product(product)
                    .build();
            WishList savedWishList = wishListRepository.save(newWishList);
            return WishListResponseDTO.from(savedWishList); // 추가
        }
    }

    @Transactional
    public void deleteAllWishList() {
        wishListRepository.deleteAll();

        wishListRepository.flush();

        wishListRepository.resetAutoIncrement();
    }


}

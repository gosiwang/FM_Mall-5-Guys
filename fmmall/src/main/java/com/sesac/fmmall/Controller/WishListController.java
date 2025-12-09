package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.WishList.WishListRequestDTO;
import com.sesac.fmmall.DTO.WishList.WishListResponseDTO;
import com.sesac.fmmall.Service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/WishList")
@RequiredArgsConstructor
public class WishListController extends BaseController {

    private final WishListService wishListService;

    /* 1. 특정 아이디로 조회 */
    @GetMapping("/find/{wishListId}")
    public ResponseEntity<WishListResponseDTO> findWishListById(@PathVariable int wishListId) {
        WishListResponseDTO resultWishList = wishListService.findWishListByWishListId(wishListId);

        return ResponseEntity.ok(resultWishList);
    }

    /* 2. 최신 생성순 정렬(페이징) -> 유저별 */
    @GetMapping("/findByUser/{userId}/{curPage}")
    public ResponseEntity<Page<WishListResponseDTO>> findWishListByUserIdSortedUpdatedAt(@PathVariable int userId, @PathVariable int curPage) {
        Page<WishListResponseDTO> resultInquiryAnswer = wishListService.findWishListByUserIdSortedCreatedAt(userId, curPage);

        return ResponseEntity.ok(resultInquiryAnswer);
    }

    /* 3. 위시리스트 등록 */
    @PostMapping("/insert")
    public ResponseEntity<WishListResponseDTO> insertWishList(@RequestBody WishListRequestDTO requestDTO) {
        WishListResponseDTO newWishList = wishListService.insertWishList(getCurrentUserId(), requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newWishList);
    }

    /* 4. 위시리스트 삭제 */
    @DeleteMapping("/delete/{wishListId}")
    public ResponseEntity<Void> deleteWishList(@PathVariable int wishListId) {

        wishListService.deleteWishList(wishListId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllWishList() {
        wishListService.deleteAllWishList();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/toggle")
    public ResponseEntity<WishListResponseDTO> toggleWishlist(@RequestBody WishListRequestDTO request) {

        WishListResponseDTO toggleWishList = wishListService.toggleWishlist(getCurrentUserId(), request);

        return ResponseEntity.ok(toggleWishList);
    }
}

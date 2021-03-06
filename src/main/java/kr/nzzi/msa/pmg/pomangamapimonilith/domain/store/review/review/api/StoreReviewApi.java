package kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.api;

import com.fasterxml.jackson.annotation.JsonView;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.dto.StoreReviewDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.dto.StoreReviewDtoView;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.model.StoreReviewSortType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.service.impl.StoreReviewServiceImpl;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/dsites/{dIdx}/stores/{sIdx}/reviews")
@AllArgsConstructor
public class StoreReviewApi {

    StoreReviewServiceImpl storeReviewService;
    UserServiceImpl userService;

    @GetMapping
    @JsonView(StoreReviewDtoView.CustomView.class)
    public ResponseEntity<?> findByIdxStore(
            @PathVariable(value = "dIdx", required = true) Long dIdx,
            @PathVariable(value = "sIdx", required = true) Long sIdx,
            @RequestParam(value = "sortType", required = false, defaultValue = "SORT_BY_DATE_DESC") StoreReviewSortType sortType,
            @PageableDefault(sort = {"idx"}, direction = Sort.Direction.DESC, size = 10) Pageable pageable,
            Principal principal
    ) {
        Long uIdx = userService.findIdxByPhoneNumber(principal.getName());
        return new ResponseEntity(storeReviewService.findByIdxStore(sIdx, uIdx, sortType, pageable), HttpStatus.OK);
    }

    @GetMapping("/{idx}")
    @JsonView(StoreReviewDtoView.CustomView.class)
    public ResponseEntity<?> findByIdx(
            @PathVariable(value = "dIdx", required = true) Long dIdx,
            @PathVariable(value = "sIdx", required = true) Long sIdx,
            @PathVariable(value = "idx", required = true) Long idx,
            Principal principal
    ) {
        Long uIdx = userService.findIdxByPhoneNumber(principal.getName());
        return new ResponseEntity(storeReviewService.findByIdx(idx, uIdx), HttpStatus.OK);
    }

    @GetMapping("/search/count")
    public ResponseEntity<?> count(
            @PathVariable(value = "dIdx", required = true) Long dIdx,
            @PathVariable(value = "sIdx", required = true) Long sIdx
    ) {
        return new ResponseEntity(storeReviewService.count(), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_USER') or hasRole('ROLE_ADMIN'))")
    @PostMapping
    @JsonView(StoreReviewDtoView.CustomView.class)
    public ResponseEntity<?> post(
            @PathVariable(value = "dIdx", required = true) Long dIdx,
            @PathVariable(value = "sIdx", required = true) Long sIdx,
            StoreReviewDto dto,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam(name = "idxesOrderItem", required = false) String idxesOrderItem,
            Principal principal
    ) {
        Long uIdx = userService.findIdxByPhoneNumber(principal.getName());
        dto.setIdxUser(uIdx);
        dto.setIdxStore(sIdx);
        dto.setIdxDeliverySite(dIdx);
        return new ResponseEntity(storeReviewService.save(dto, images, idxesOrderItem), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_USER') or hasRole('ROLE_ADMIN'))")
    @PatchMapping("/{rIdx}")
    @JsonView(StoreReviewDtoView.CustomView.class)
    public ResponseEntity<?> patch(
            @PathVariable(value = "dIdx", required = true) Long dIdx,
            @PathVariable(value = "sIdx", required = true) Long sIdx,
            @PathVariable(value = "rIdx", required = true) Long rIdx,
            StoreReviewDto dto,
            @RequestParam("images") List<MultipartFile> images,
            Principal principal
    ) {
        Long uIdx = userService.findIdxByPhoneNumber(principal.getName());
        if(storeReviewService.findByIdx(rIdx, uIdx).getIsOwn()) {
            dto.setIdx(rIdx);
            dto.setIdxUser(uIdx);
            dto.setIdxStore(sIdx);
            dto.setIdxDeliverySite(dIdx);
            return new ResponseEntity(storeReviewService.update(dto, images), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_USER') or hasRole('ROLE_ADMIN'))")
    @DeleteMapping("/{rIdx}")
    public ResponseEntity<?> delete(
            @PathVariable(value = "dIdx", required = true) Long dIdx,
            @PathVariable(value = "sIdx", required = true) Long sIdx,
            @PathVariable(value = "rIdx", required = true) Long rIdx,
            Principal principal
    ) {
        Long uIdx = userService.findIdxByPhoneNumber(principal.getName());
        if(storeReviewService.findByIdx(rIdx, uIdx).getIsOwn()) {
            storeReviewService.delete(dIdx, sIdx, rIdx);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}

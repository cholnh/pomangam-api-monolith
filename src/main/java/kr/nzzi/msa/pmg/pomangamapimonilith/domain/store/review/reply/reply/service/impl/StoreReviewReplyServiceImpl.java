package kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.reply.reply.service.impl;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.reply.like.dao.jpa.StoreReviewReplyLikeJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.reply.reply.dao.jpa.StoreReviewReplyJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.reply.reply.dto.StoreReviewReplyDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.reply.reply.model.StoreReviewReply;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.reply.reply.service.StoreReviewReplyService;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.dao.jpa.StoreReviewJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.model.StoreReview;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.dao.jpa.UserJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StoreReviewReplyServiceImpl implements StoreReviewReplyService {

    StoreReviewReplyJpaRepository storeReviewReplyRepo;
    UserJpaRepository userRepo;
    StoreReviewJpaRepository storeReviewRepo;
    StoreReviewReplyLikeJpaRepository storeReviewReplyLikeRepo;

    @Override
    public List<StoreReviewReplyDto> findByIdxStoreReview(Long rIdx, Long uIdx, Pageable pageable) {
        List<StoreReviewReply> entities = storeReviewReplyRepo.findByIdxStoreReviewAndIsActiveIsTrue(rIdx, pageable).getContent();
        return fromEntitiesCustom(entities, uIdx);
    }

    @Override
    public StoreReviewReplyDto findByIdx(Long idx, Long uIdx) {
        StoreReviewReply entity = storeReviewReplyRepo.findByIdxAndIsActiveIsTrue(idx);
        return fromEntityCustom(entity, uIdx);
    }

    @Override
    public long count() {
        return storeReviewReplyRepo.countByIsActiveIsTrue();
    }

    @Override
    @Transactional
    public StoreReviewReplyDto save(StoreReviewReplyDto dto) {
        // ?????? ??????
        StoreReviewReply entity = storeReviewReplyRepo.save(dto.toEntity());

        // ??? ?????? ??? ??????
        addCntReply(dto.getIdxStoreReview());

        return StoreReviewReplyDto.fromEntity(entity);
    }

    @Override
    @Transactional
    public StoreReviewReplyDto update(StoreReviewReplyDto dto) {
        // ?????? ??????
        StoreReviewReply entity = storeReviewReplyRepo.findByIdxAndIsActiveIsTrue(dto.getIdx());
        return StoreReviewReplyDto.fromEntity(storeReviewReplyRepo.save(entity.update(dto.toEntity())));
    }

    @Override
    @Transactional
    public void delete(Long rIdx, Long idx) {
        // ??? ?????? ??? ??????
        subCntReply(rIdx);

        // ?????? ??????
        storeReviewReplyRepo.deleteById(idx);
    }

    /**
     * ?????? ?????? ??? ??????
     */
    private void addCntReply(Long idxReview) {
        StoreReview storeReview = storeReviewRepo.findByIdxAndIsActiveIsTrue(idxReview);
        storeReview.addCntReply();
        storeReviewRepo.save(storeReview);
    }

    /**
     * ?????? ?????? ??? ??????
     */
    private void subCntReply(Long idxReview) {
        StoreReview storeReview = storeReviewRepo.findByIdxAndIsActiveIsTrue(idxReview);
        storeReview.subCntReply();
        storeReviewRepo.save(storeReview);
    }

    /**
     * entity -> dto ??????
     * ???????????? ?????????
     *
     * @param entities ????????? ?????????
     * @return dto ?????????
     */
    private List<StoreReviewReplyDto> fromEntitiesCustom(List<StoreReviewReply> entities, Long uIdx) {
        List<StoreReviewReplyDto> dtos = new ArrayList<>();
        for(StoreReviewReply entity : entities) {
            StoreReviewReplyDto dto = fromEntityCustom(entity, uIdx);
            if(dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * entity -> dto ??????
     * ???????????? ?????????
     * Todo. "??????" -> Globalization ?????? ??????
     *
     * @param entity ????????? ?????????
     * @return dto ??????. ?????? User ??? ???????????? ???????????? null ??????
     */
    private StoreReviewReplyDto fromEntityCustom(StoreReviewReply entity, Long uIdx) {
        StoreReviewReplyDto dto = StoreReviewReplyDto.fromEntity(entity);
        User user = userRepo.findByIdxAndIsActiveIsTrue(entity.getIdxUser());
        if (uIdx != null && uIdx.compareTo(user.getIdx()) == 0) {  // isOwn ??????
            dto.setIsOwn(true);
            dto.setIsLike(storeReviewReplyLikeRepo.existsByIdxUserAndIdxStoreReviewReply(uIdx, entity.getIdx()));
            if (entity.getIsAnonymous()) { // anonymous ??????
                dto.setNickname("??? (??????)");
            } else {
                dto.setNickname(user.getNickname());
            }
        } else {
            dto.setIsOwn(false);
            dto.setIsLike(false);
            if (entity.getIsAnonymous()) { // anonymous ??????
                dto.setNickname("??????");
            } else {
                dto.setNickname(user.getNickname());
            }
        }
        return dto;
    }
}

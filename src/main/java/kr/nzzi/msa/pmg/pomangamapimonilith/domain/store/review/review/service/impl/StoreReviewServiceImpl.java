package kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.service.impl;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.file.service.impl.FileStorageServiceImpl;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.item.item.dao.jpa.OrderItemJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.item.item.model.OrderItem;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.image.model.StoreReviewImage;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.image.model.StoreReviewImageType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.like.dao.jpa.StoreReviewLikeJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.reply.reply.service.impl.StoreReviewReplyServiceImpl;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.dao.jpa.StoreReviewJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.dto.StoreReviewDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.model.StoreReview;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.model.StoreReviewSortType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.service.ImagePath;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.review.review.service.StoreReviewService;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.dao.jpa.StoreJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.Store;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.dao.jpa.UserJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.model.User;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StoreReviewServiceImpl implements StoreReviewService {

    StoreJpaRepository storeRepo;
    StoreReviewJpaRepository storeReviewRepo;
    StoreReviewLikeJpaRepository storeReviewLikeRepo;
    StoreReviewReplyServiceImpl storeReviewReplyService;
    UserJpaRepository userRepo;
    FileStorageServiceImpl fileStorageService;
    OrderItemJpaRepository orderItemRepo;

    @Override
    public List<StoreReviewDto> findByIdxStore(Long sIdx, Long uIdx, StoreReviewSortType sortType, Pageable pageable) {
        List<StoreReview> entities = storeReviewRepo.findByIdxStore(sIdx, sortType, pageable).getContent();
        return fromEntitiesCustom(entities, uIdx);
    }

    @Override
    public StoreReviewDto findByIdx(Long idx, Long uIdx) {
        StoreReview entity = storeReviewRepo.findByIdxAndIsActiveIsTrue(idx);
        return fromEntityCustom(entity, uIdx);
    }

    @Override
    public long count() {
        return storeReviewRepo.countByIsActiveIsTrue();
    }

    @Override
    @Transactional
    public StoreReviewDto save(StoreReviewDto dto,  List<MultipartFile> images, String idxesOrderItem) {
        // ?????? ??????
        StoreReview entity = storeReviewRepo.save(dto.toEntity());

        // ?????? ?????? ??? ??????, ??? ?????? ??? ??????
        addAvgStar(dto.getIdxStore(), entity.getIdx());

        // ?????? ????????? ??????
        if(images != null) {
            String imagePath = ImagePath.reviews(dto.getIdxStore(), entity.getIdx());
            List<StoreReviewImage> savedImages = saveImage(imagePath, images);
            entity.addImages(savedImages);
        }

        // order item isReviewWrite ??????
        for(String idxOrderItem : idxesOrderItem.split(",")) {
            OrderItem item = orderItemRepo.findById(Long.parseLong(idxOrderItem))
                    .orElseThrow(() -> new RuntimeException("invalid order item index"));
            item.setIsReviewWrite(true);
            orderItemRepo.save(item);
        }

        return StoreReviewDto.fromEntity(storeReviewRepo.save(entity));
    }

    @Override
    @Transactional
    public StoreReviewDto update(StoreReviewDto dto, List<MultipartFile> images) {
        // ?????? ??????
        StoreReview entity = storeReviewRepo.findByIdxAndIsActiveIsTrue(dto.getIdx());
        entity = storeReviewRepo.save(entity.update(dto.toEntity()));

        // ?????? ????????? ??????
        boolean isImageUpdated = dto.getIsImageUpdated() != null && dto.getIsImageUpdated().booleanValue();
        if(isImageUpdated) {
            // ?????? ????????? ?????? ??????
            String imagePath = ImagePath.reviews(dto.getIdxStore(), dto.getIdx());
            fileStorageService.deleteFile(imagePath, true);
            entity.clearImages();

            if(images.size() > 0) {
                // ????????? ????????? ?????? ??????
                List<StoreReviewImage> savedImages = saveImage(imagePath, images);
                entity.addImages(savedImages);
            }
        }
        return StoreReviewDto.fromEntity(storeReviewRepo.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long dIdx, Long sIdx, Long idx) {
        // ?????? ?????? ??? ??????, ??? ?????? ??? ??????
        subAvgStar(sIdx, idx);

        // ?????? ??????
        storeReviewRepo.deleteById(idx);

        // ?????? ????????? ??????
        String imagePath = ImagePath.reviews(sIdx, idx);
        fileStorageService.deleteFile(imagePath, true);
    }

    private List<StoreReviewImage> saveImage(String imagePath, List<MultipartFile> images) {
        List<StoreReviewImage> storeReviewImages = new ArrayList<>();
        for(int i=0; i<images.size(); i++) {
            MultipartFile image = images.get(i);
            String fileName = (i+1) + "." + FilenameUtils.getExtension(image.getOriginalFilename());

            // ?????? ????????? ?????? ??????
            fileStorageService.storeFile(image, imagePath, fileName);

            // ?????? ????????? DB ??????
            storeReviewImages.add(StoreReviewImage.builder()
                    .imagePath(imagePath + fileName)
                    .imageType(i==0 ? StoreReviewImageType.MAIN : StoreReviewImageType.SUB)
                    .sequence(i+1)
                    .build());
        }
        return storeReviewImages;
    }

    /**
     * ?????? ?????? ????????? (add)
     */
    private void addAvgStar(Long sIdx, Long rIdx) {
        Store store = storeRepo.findByIdxAndIsActiveIsTrue(sIdx);
        StoreReview storeReview = storeReviewRepo.findByIdxAndIsActiveIsTrue(rIdx);
        store.addCntReview(storeReview.getStar());
        storeRepo.save(store);
    }

    /**
     * ?????? ?????? ????????? (sub)
     */
    private void subAvgStar(Long sIdx, Long rIdx) {
        Store store = storeRepo.findByIdxAndIsActiveIsTrue(sIdx);
        StoreReview storeReview = storeReviewRepo.findByIdxAndIsActiveIsTrue(rIdx);
        store.subCntReview(storeReview.getStar());
        storeRepo.save(store);
    }

    /**
     * entity -> dto ??????
     * ???????????? ?????????
     *
     * @param entities ????????? ?????????
     * @return dto ?????????
     */
    private List<StoreReviewDto> fromEntitiesCustom(List<StoreReview> entities, Long uIdx) {
        List<StoreReviewDto> dtos = new ArrayList<>();
        for(StoreReview entity : entities) {
            StoreReviewDto dto = fromEntityCustom(entity, uIdx);
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
    private StoreReviewDto fromEntityCustom(StoreReview entity, Long uIdx) {
        StoreReviewDto dto = StoreReviewDto.fromEntity(entity);
        User user = userRepo.findByIdxAndIsActiveIsTrue(entity.getIdxUser());
        if (uIdx != null && uIdx.compareTo(user.getIdx()) == 0) {  // isOwn ??????
            dto.setIsOwn(true);
            dto.setIsLike(storeReviewLikeRepo.existsByIdxUserAndIdxStoreReview(uIdx, entity.getIdx()));
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

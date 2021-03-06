package kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.reply.reply.service.impl;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.product.dao.jpa.ProductJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.product.model.Product;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.reply.like.dao.jpa.ProductReplyLikeJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.reply.reply.dao.jpa.ProductReplyJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.reply.reply.dto.ProductReplyDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.reply.reply.model.ProductReply;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.reply.reply.service.ProductReplyService;
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
public class ProductReplyServiceImpl implements ProductReplyService {

    ProductReplyJpaRepository productReplyRepo;
    ProductReplyLikeJpaRepository productReplyLikeRepo;
    UserJpaRepository userRepo;
    ProductJpaRepository productRepo;

    @Override
    public List<ProductReplyDto> findByIdxProduct(Long pIdx, Long uIdx, Pageable pageable) {
        List<ProductReply> entities = productReplyRepo.findByIdxProductAndIsActiveIsTrue(pIdx, pageable).getContent();
        return fromEntitiesCustom(entities, uIdx);
    }

    @Override
    public ProductReplyDto findByIdx(Long idx, Long uIdx) {
        ProductReply entity = productReplyRepo.findByIdxAndIsActiveIsTrue(idx);
        return fromEntityCustom(entity, uIdx);
    }

    @Override
    public long count() {
        return productReplyRepo.countByIsActiveIsTrue();
    }

    @Override
    @Transactional
    public ProductReplyDto save(ProductReplyDto dto) {
        // ?????? ??????
        ProductReply entity = productReplyRepo.save(dto.toEntity());

        // ??? ?????? ??? ??????
        addCntReply(dto.getIdxProduct());

        return ProductReplyDto.fromEntity(entity);
    }

    @Override
    @Transactional
    public ProductReplyDto update(ProductReplyDto dto) {
        // ?????? ??????
        ProductReply entity = productReplyRepo.findByIdxAndIsActiveIsTrue(dto.getIdx());
        return ProductReplyDto.fromEntity(productReplyRepo.save(entity.update(dto.toEntity())));
    }

    @Override
    @Transactional
    public void delete(Long rIdx, Long idx) {
        // ??? ?????? ??? ??????
        subCntReply(rIdx);

        // ?????? ??????
        productReplyRepo.deleteById(idx);
    }

    /**
     * ?????? ?????? ??? ??????
     */
    private void addCntReply(Long idxProduct) {
        Product product = productRepo.findByIdxAndIsActiveIsTrue(idxProduct);
        product.addCntReply();
        productRepo.save(product);
    }

    /**
     * ?????? ?????? ??? ??????
     */
    private void subCntReply(Long idxProduct) {
        Product product = productRepo.findByIdxAndIsActiveIsTrue(idxProduct);
        product.subCntReply();
        productRepo.save(product);
    }

    /**
     * entity -> dto ??????
     * ???????????? ?????????
     *
     * @param entities ????????? ?????????
     * @return dto ?????????
     */
    private List<ProductReplyDto> fromEntitiesCustom(List<ProductReply> entities, Long uIdx) {
        List<ProductReplyDto> dtos = new ArrayList<>();
        for(ProductReply entity : entities) {
            ProductReplyDto dto = fromEntityCustom(entity, uIdx);
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
    private ProductReplyDto fromEntityCustom(ProductReply entity, Long uIdx) {
        ProductReplyDto dto = ProductReplyDto.fromEntity(entity);
        User user = userRepo.findByIdxAndIsActiveIsTrue(entity.getIdxUser());
        if (uIdx != null && uIdx.compareTo(user.getIdx()) == 0) {  // isOwn ??????
            dto.setIsOwn(true);
            dto.setIsLike(productReplyLikeRepo.existsByIdxUserAndIdxProductReply(uIdx, entity.getIdx()));
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

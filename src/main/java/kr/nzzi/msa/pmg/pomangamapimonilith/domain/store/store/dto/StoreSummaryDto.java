package kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.dto;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.image.model.StoreImage;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.Store;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.info.ProductionInfo;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.info.StoreInfo;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.schedule.StoreSchedule;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class StoreSummaryDto implements Serializable {

    private Long idx;
    private String name;
    private String description;
    private String subDescription;
    private ProductionInfo productionInfo;
    private StoreSchedule storeSchedule;
    private Float avgStar;
    private Integer cntLike;
    private Integer cntReview;
    private Integer cntOrder;
    private Integer sequence;

    // 추가 사항
    private String brandImagePath;
    private String storeImageMainPath;
    private List<String> storeImageSubPaths = new ArrayList<>();
    private Integer promotionType;
    private Integer promotionValue; // promotionType: 0 -> 할인안함 / 1 -> 할인가(단위: 원) / 2 -> 할인률(단위: %)
    private Integer couponType;
    private Integer couponValue;
    private Integer quantityOrderable;  // 주문 가능 수량
    private LocalDateTime modifyDate;

    public static StoreSummaryDto fromEntity(Store entity) {
        StoreSummaryDto dto = new ModelMapper().map(entity, StoreSummaryDto.class);

        // images
        List<StoreImage> storeImages = entity.getImages();
        if(storeImages != null && !storeImages.isEmpty()) {
            for(StoreImage storeImage : storeImages) {
                switch (storeImage.getImageType()) {
                    case MAIN:
                        dto.setStoreImageMainPath(storeImage.getImagePath()+"?v="+storeImage.getModifyDate());
                        break;
                    case SUB:
                        dto.getStoreImageSubPaths().add(storeImage.getImagePath()+"?v="+storeImage.getModifyDate());
                        break;
                    case BRAND:
                        dto.setBrandImagePath(storeImage.getImagePath()+"?v="+storeImage.getModifyDate());
                        break;
                }
            }
        }

        // promotion
        dto.setPromotionType(0);
        dto.setPromotionValue(0);

        // coupon
        dto.setCouponType(0);
        dto.setCouponValue(0);

        // info
        StoreInfo storeInfo = entity.getStoreInfo();
        dto.setName(storeInfo.getName());
        dto.setDescription(storeInfo.getDescription());
        dto.setSubDescription(storeInfo.getSubDescription());

        dto.setModifyDate(entity.getModifyDate());

        return dto;
    }

    public static List<StoreSummaryDto> fromEntities(List<Store> entities) {
        List<StoreSummaryDto> dtos = new ArrayList<>();
        for(Store entity : entities) {
            dtos.add(fromEntity(entity));
        }
        return dtos;
    }
}

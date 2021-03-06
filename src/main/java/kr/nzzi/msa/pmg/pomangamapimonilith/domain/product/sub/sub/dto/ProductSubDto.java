package kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.sub.dto;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.image.model.ProductSubImage;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.sub.model.ProductSub;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.sub.model.ProductSubType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.sub.model.info.ProductSubInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductSubDto implements Serializable {

    private Long idx;
    private LocalDateTime registerDate;
    private LocalDateTime modifyDate;
    private Long idxStore;
    private Integer salePrice;
    private ProductSubInfo productSubInfo;
    private Integer sequence;
    private String productSubCategory;
    private Integer numberMinimum;
    private Integer numberMaximum;

    // images
    private String productImageMainPath;
    private List<String> productImageSubPaths = new ArrayList<>();

    private Boolean isTempActive;

    public ProductSub toEntity() {
        ProductSub entity = new ModelMapper().map(this, ProductSub.class);
        return entity;
    }

    public static ProductSubDto fromEntity(ProductSub entity) {
        if(entity == null) return null;
        ProductSubDto dto = new ModelMapper().map(entity, ProductSubDto.class);

        // images
        if(entity.getProductSubCategory().getProductSubType() == ProductSubType.CUSTOMIZING_SUB) {
            List<ProductSubImage> productSubImages = entity.getImages();
            if(productSubImages != null && !productSubImages.isEmpty()) {
                for(ProductSubImage productSubImage : productSubImages) {
                    switch (productSubImage.getImageType()) {
                        case MAIN:
                            dto.setProductImageMainPath(productSubImage.getImagePath()+"?v="+productSubImage.getModifyDate());
                            break;
                        case SUB:
                            dto.getProductImageSubPaths().add(productSubImage.getImagePath()+"?v="+productSubImage.getModifyDate());
                            break;
                    }
                }
            }
        }

        // category
        dto.setProductSubCategory(entity.getProductSubCategory().getCategoryTitle());

        // cost
        dto.setSalePrice(entity.getCost().saleCost());

        return dto;
    }

    public static List<ProductSubDto> fromEntities(List<ProductSub> entities) {
        if(entities == null) return null;
        List<ProductSubDto> dtos = new ArrayList<>();
        for(ProductSub entity : entities) {
            dtos.add(fromEntity(entity));
        }
        return dtos;
    }
}

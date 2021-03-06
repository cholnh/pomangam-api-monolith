package kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.category.dto;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.category.model.ProductSubCategory;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.sub.dto.ProductSubDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.sub.sub.model.ProductSubType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductSubCategoryDto implements Serializable {

    private Long idx;
    private LocalDateTime registerDate;
    private LocalDateTime modifyDate;
    private String categoryTitle;
    private Boolean isNecessary;
    private ProductSubType productSubType;
    private List<ProductSubDto> productSubs;

    public ProductSubCategory toEntity() {
        ProductSubCategory entity = new ModelMapper().map(this, ProductSubCategory.class);
        return entity;
    }

    public static ProductSubCategoryDto fromEntity(ProductSubCategory entity) {
        if(entity == null) return null;
        ProductSubCategoryDto dto = new ProductSubCategoryDto();
        dto.setIdx(entity.getIdx());
        dto.setRegisterDate(entity.getRegisterDate());
        dto.setModifyDate(entity.getModifyDate());
        dto.setCategoryTitle(entity.getCategoryTitle());
        dto.setIsNecessary(entity.getIsNecessary());
        dto.setProductSubType(entity.getProductSubType());
        dto.setProductSubs(ProductSubDto.fromEntities(entity.getProductSubs()));
        return dto;
    }

    public static List<ProductSubCategoryDto> fromEntities(List<ProductSubCategory> entities) {
        if(entities == null) return null;
        List<ProductSubCategoryDto> dtos = new ArrayList<>();
        for(ProductSubCategory entity : entities) {
            dtos.add(fromEntity(entity));
        }
        return dtos;
    }
}
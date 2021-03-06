package kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model;

import kr.nzzi.msa.pmg.pomangamapimonilith.global._base.Auditable;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.product.category.model.ProductCategory;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.category.model.StoreCategory;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.image.model.StoreImage;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.info.ProductionInfo;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.info.StoreInfo;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.schedule.StoreSchedule;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.story.story.model.StoreStory;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "store_tbl")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"storeCategory", "images"})
public class Store extends Auditable {

    /**
     * 업체 분류
     */
    @JoinColumn(name = "idx_store_category")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private StoreCategory storeCategory;

    /**
     * 업체 정보
     */
    @Embedded
    private StoreInfo storeInfo;

    /**
     * 업체 생산량
     */
    @Embedded
    private ProductionInfo productionInfo;

    /**
     * 업체 영업 시간
     */
    @Embedded
    private StoreSchedule storeSchedule;

    /**
     * 평균 리뷰 평점
     */
    @Column(name = "avg_star", nullable = false, columnDefinition = "FLOAT default 0")
    private Float avgStar;

    /**
     * 총 좋아요 개수
     */
    @Column(name = "cnt_like", nullable = false, columnDefinition = "INT default 0")
    private Integer cntLike;

    /**
     * 총 리뷰 개수
     */
    @Column(name = "cnt_review", nullable = false, columnDefinition = "INT default 0")
    private Integer cntReview;

    /**
     * 총 주문 개수
     */
    @Column(name = "cnt_order", nullable = false, columnDefinition = "INT default 0")
    private Integer cntOrder;

    /**
     * 순서
     */
    @Column(name = "sequence", nullable = false, columnDefinition = "INT default 0")
    private Integer sequence;

    /**
     * 이미지 정보
     * 단방향 매핑
     */
    @JoinColumn(name = "idx_store", nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence ASC")
    private List<StoreImage> images = new ArrayList<>();

    /**
     * 제품 카테고리 정보
     * 단방향 매핑
     */
    @JoinColumn(name = "idx_store", nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idx ASC")
    private List<ProductCategory> productCategories = new ArrayList<>();

    /**
     * 스토리
     * 단방향 매핑
     */
    @JoinColumn(name = "idx_store", nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence ASC")
    private List<StoreStory> stories = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        // always 0 when its insert
//        this.avgStar = 0f;
//        this.cntLike = 0;
//        this.cntComment = 0;
        this.sequence = sequence == null ? 0 : sequence;
    }

    @Builder
    public Store(Long idx, StoreCategory storeCategory, StoreInfo storeInfo, ProductionInfo productionInfo, StoreSchedule storeSchedule, Float avgStar, Integer cntLike, Integer cntReview, Integer cntOrder, Integer sequence, List<StoreImage> images, List<ProductCategory> productCategories, List<StoreStory> stories) {
        super.setIdx(idx);
        this.storeCategory = storeCategory;
        this.storeInfo = storeInfo;
        this.productionInfo = productionInfo;
        this.storeSchedule = storeSchedule;
        this.avgStar = avgStar;
        this.cntLike = cntLike;
        this.cntReview = cntReview;
        this.cntOrder = cntOrder;
        this.sequence = sequence;
        this.images = images;
        this.productCategories = productCategories;
        this.stories = stories;
    }

    public void addImages(StoreImage ...storeImage) {
        if(this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.addAll(Arrays.asList(storeImage));
    }
    public void addProductCategories(ProductCategory ...productCategory) {
        if(this.productCategories == null) {
            this.productCategories = new ArrayList<>();
        }
        this.productCategories.addAll(Arrays.asList(productCategory));
    }
    public void addStories(StoreStory ...storeStory) {
        if(this.stories == null) {
            this.stories = new ArrayList<>();
        }
        this.stories.addAll(Arrays.asList(storeStory));
    }
    public void addCntLike() {
        if(this.cntLike == null) {
            this.cntLike = 0;
        }
        this.cntLike++;
    }
    public void addCntReview(Float star) {
        if(this.cntReview == null) {
            this.cntReview = 0;
        }
        if(this.avgStar == null) {
            this.avgStar = 0f;
        }
        this.avgStar = ((avgStar * cntReview) + star) / (++this.cntReview);
    }
    public void subCntLike() {
        if(this.cntLike == null) {
            this.cntLike = 0;
        }
        this.cntLike--;
        if(this.cntLike < 0) {
            this.cntLike = 0;
        }
    }
    public void subCntReview(Float star) {
        if(this.cntReview == null || this.cntReview <= 1) {
            this.cntReview = 0;
            this.avgStar = 0f;
        } else if(this.avgStar == null || this.avgStar <= 0) {
            this.avgStar = 0f;
        } else {
            this.avgStar = ((avgStar * cntReview) - star) / (--this.cntReview);
        }
    }

}

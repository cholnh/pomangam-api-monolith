package kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.point.rank.model;

import kr.nzzi.msa.pmg.pomangamapimonilith.global._base.Auditable;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "point_rank_tbl")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PointRank extends Auditable {

    /**
     * 계급 레벨
     */
    @Column(name = "level", nullable = false)
    private Short level;

    /**
     * 계급 타이틀
     */
    @Column(name = "title", nullable = false, length = 20)
    private String title;

    /**
     * 등업 조건 - (다음레벨) 최소 누적 주문 횟수
     */
    @Column(name = "next_lower_limit_order_count", nullable = false)
    private Integer nextLowerLimitOrderCount;

    /**
     * 등업 조건 - (다음레벨) 최소 누적 추천인 수
     */
    @Column(name = "next_lower_limit_recommend_count", nullable = false)
    private Integer nextLowerLimitRecommendCount;

    /**
     * 등업 혜택 - 제공 쿠폰가격
     */
    @Column(name = "reward_coupon_price", nullable = false)
    private Integer rewardCouponPrice;

    /**
     * 혜택 - 포인트 적립률
     */
    @Column(name = "percent_save_point", nullable = false)
    private Float percentSavePoint;

    /**
     * 혜택 - 포인트 적립 금액
     */
    @Column(name = "price_save_point", nullable = false)
    private Integer priceSavePoint;

    @Builder
    public PointRank(Long idx, Short level, String title, Integer nextLowerLimitOrderCount, Integer nextLowerLimitRecommendCount, Integer rewardCouponPrice, Float percentSavePoint, Integer priceSavePoint) {
        super.setIdx(idx);
        this.level = level;
        this.title = title;
        this.nextLowerLimitOrderCount = nextLowerLimitOrderCount;
        this.nextLowerLimitRecommendCount = nextLowerLimitRecommendCount;
        this.rewardCouponPrice = rewardCouponPrice;
        this.percentSavePoint = percentSavePoint;
        this.priceSavePoint = priceSavePoint;
    }
}

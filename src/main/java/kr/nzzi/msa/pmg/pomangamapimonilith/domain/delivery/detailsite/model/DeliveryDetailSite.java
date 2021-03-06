package kr.nzzi.msa.pmg.pomangamapimonilith.domain.delivery.detailsite.model;

import kr.nzzi.msa.pmg.pomangamapimonilith.global._base.Auditable;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.delivery.deliverysite.model.DeliverySite;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "delivery_detail_site_tbl")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"deliverySite"})
public class DeliveryDetailSite extends Auditable {

    /**
     * 배달지
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idx_delivery_site")
    private DeliverySite deliverySite;

    /**
     * 배달지명
     * 배달이 도착하는 기관의 이름
     * 글자수: utf8 기준 / 영문 20자 / 한글 20자
     */
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    /**
     * 상세 주소 설명
     * 글자수: utf8 기준 / 영문 100자 / 한글 100자
     */
    @Column(name = "location", nullable = false, length = 100)
    private String location;

    /**
     * 순서
     */
    @Column(name = "sequence", nullable = false, columnDefinition = "INT default 0")
    private Integer sequence;

    /**
     * 배달 추가 시간
     */
    @Column(name = "additional_time", nullable = false)
    private LocalTime additionalTime;

    /**
     * gps 위도
     */
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    /**
     * gps 경도
     */
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    /**
     * 축약어 (초성 등)
     * 글자수: utf8 기준 / 영문 5자 / 한글 5자
     */
    @Column(name = "abbreviation", nullable = false, length = 5)
    private String abbreviation;

    @Builder
    public DeliveryDetailSite(Long idx, DeliverySite deliverySite, String name, String location, Integer sequence, LocalTime additionalTime, Double latitude, Double longitude, String abbreviation) {
        super.setIdx(idx);
        this.deliverySite = deliverySite;
        this.name = name;
        this.location = location;
        this.sequence = sequence;
        this.additionalTime = additionalTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.abbreviation = abbreviation;
    }

    public String getFullName() {
        return deliverySite.getName() + " " + name;
    }
}

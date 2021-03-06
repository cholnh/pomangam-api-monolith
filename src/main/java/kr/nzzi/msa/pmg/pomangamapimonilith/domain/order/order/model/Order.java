package kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model;

import kr.nzzi.msa.pmg.pomangamapimonilith.global._base.Auditable;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.delivery.detailsite.model.DeliveryDetailSite;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.item.item.model.OrderItem;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.orderer.Orderer;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.payment_info.PaymentInfo;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.ordertime.ordertime.model.OrderTime;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_tbl")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Order extends Auditable {

    /**
     * 주문 타입
     */
    @Column(name = "order_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    /**
     * 주문 식별 번호 (고객식별용)
     */
    @Column(name = "box_number", nullable = false, columnDefinition = "SMALLINT default 0")
    private Short boxNumber;

    /**
     * 주문자 정보
     */
    @Embedded
    private Orderer orderer;

    /**
     * 결제 정보
     */
    @Embedded
    private PaymentInfo paymentInfo;

    /**
     * 받는 장소 (상세 배달지)
     * 단방향 매핑
     */
    @JoinColumn(name = "idx_delivery_detail_site")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private DeliveryDetailSite deliveryDetailSite;

    /**
     * 받는 날짜
     */
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * 받는 시간
     * 단방향 매핑
     */
    @JoinColumn(name = "idx_order_time")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OrderTime orderTime;

    /**
     * 주문 아이템 리스트
     * 단방향 매핑
     */
    @JoinColumn(name = "idx_order", nullable = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("idx ASC")
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * PG에서 부여하는 결제 고유 id
     */
    @Column(name = "receipt_id", nullable = true, length = 255)
    private String receiptId;

    /**
     * 결제금액
     */
    @Column(name = "payment_cost", nullable = true, columnDefinition = "INT default 0")
    private Integer paymentCost;

    /**
     * 비고란
     */
    @Column(name = "note", nullable = true, length = 255)
    private String note;

    /**
     * 고객이 결제해야 할 요금 반환
     * @return 결제 총 요금
     */
    public Integer paymentCost() {
        Integer total = totalCost();
        if(total != null) {
            total -= discountCost();
            return total > 0 ? total : 0;
        }
        return null;
    }

    /**
     * 주문 내역 총 가격 반환
     * @return 주문 내역 총 가격
     */
    public Integer totalCost() {
        Integer total = null;
        if(this.orderItems != null) {
            total = 0;
            for(OrderItem item : this.orderItems) {
                total += item.paymentCost();
            }
        }
        return total;
    }

    /**
     * 할인 내역 총 가격 반환
     * @return 할인 내역 총 가격
     */
    public int discountCost() {
        int q = 0;
        for(OrderItem item : orderItems) {
            q += item.getQuantity();
        }
        return paymentInfo.discountCost(q);
    }

    @Builder
    public Order(Long idx, OrderType orderType, Short boxNumber, Orderer orderer, PaymentInfo paymentInfo, DeliveryDetailSite deliveryDetailSite, LocalDate orderDate, OrderTime orderTime, List<OrderItem> orderItems, String receiptId, Integer paymentCost, String note) {
        super.setIdx(idx);
        this.orderType = orderType;
        this.boxNumber = boxNumber;
        this.orderer = orderer;
        this.paymentInfo = paymentInfo;
        this.deliveryDetailSite = deliveryDetailSite;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderItems = orderItems;
        this.receiptId = receiptId;
        this.paymentCost = paymentCost;
        this.note = note;
    }

    public void addItem(OrderItem orderItem) {
        if(this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        if(orderItem != null) {
            this.orderItems.add(orderItem);
        }
    }

}

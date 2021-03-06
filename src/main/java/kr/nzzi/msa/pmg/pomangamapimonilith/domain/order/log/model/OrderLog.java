package kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.log.model;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.OrderType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_log_tbl")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class OrderLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    /**
     * 주문 인덱스
     */
    @Column(name = "idx_order", nullable = false)
    private Long idxOrder;

    /**
     * 주문 타입
     */
    @Column(name = "order_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    /**
     * 등록 날짜
     */
    @Column(name = "register_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime registerDate;

    @Builder
    public OrderLog(Long idxOrder, OrderType orderType, LocalDateTime registerDate) {
        this.idxOrder = idxOrder;
        this.orderType = orderType;
        this.registerDate = registerDate;
    }
}

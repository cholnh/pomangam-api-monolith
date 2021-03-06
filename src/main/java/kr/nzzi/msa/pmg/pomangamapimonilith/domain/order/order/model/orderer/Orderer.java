package kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.orderer;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 상속한 프록시 객체에서만 사용
@Embeddable
public class Orderer {

    /**
     * 비회원 주문 (OrdererType.GUEST), 회원 주문(OrdererType.USER)
     */
    @Column(name="orderer_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrdererType ordererType;

    /**
     * 비회원 인덱스
     * FcmToken 인덱스로 대체함.
     * 회원 주문 또한 비회원 인덱스를 채워넣어야함.
     */
    @Column(name="idx_fcm_token", nullable = true)
    private Long idxFcmToken;

    /**
     * 회원 정보
     * 단방향 매핑
     */
    @JoinColumn(name = "idx_user")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private User user;

    /**
     * 주문자 핸드폰번호
     */
    @Column(name = "phone_number", nullable = true, length = 15)
    private String phoneNumber;

    @Builder
    public Orderer(OrdererType ordererType, Long idxFcmToken, User user, String phoneNumber) {
        this.ordererType = ordererType;
        this.idxFcmToken = idxFcmToken;
        this.user = user;
        this.phoneNumber = phoneNumber;
    }
}

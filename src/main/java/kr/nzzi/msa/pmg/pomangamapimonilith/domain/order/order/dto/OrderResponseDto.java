package kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.dto;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.coupon.coupon.dto.CouponDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.coupon.coupon.model.Coupon;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.coupon.mapper.model.CouponMapper;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.delivery.deliverysite.model.DeliverySite;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.delivery.detailsite.model.DeliveryDetailSite;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.item.item.dto.OrderItemResponseDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.item.item.model.OrderItem;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.Order;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.OrderType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.cash_receipt.CashReceiptType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.orderer.OrdererType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.payment_info.PaymentInfo;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.payment_info.PaymentType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.ordertime.ordertime.model.OrderTime;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.promotion.mapper.model.PromotionMapper;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.promotion.promotion.dto.PromotionDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.promotion.promotion.model.Promotion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderResponseDto implements Serializable {

    private Long idx;
    private LocalDateTime registerDate;
    private LocalDateTime modifyDate;

    // 주문 기본 정보
    private OrderType orderType;
    private Short boxNumber;
    private PaymentType paymentType;
    private OrdererType ordererType;
    private String ordererName;
    private String ordererPn;
    private String note;
    private String phoneNumber;

    // 결제 정보
    private Integer usingPoint;
    private List<CouponDto> usingCoupons = new ArrayList<>();
    private List<PromotionDto> usingPromotions = new ArrayList<>();
    private Integer savedPoint;
    private String cashReceipt;
    private CashReceiptType cashReceiptType;
    private Integer totalCost;
    private Integer discountCost;
    private Integer paymentCost;

    // 받는 장소
    private Long idxDeliverySite;
    private Long idxDeliveryDetailSite;
    private String nameDeliverySite;
    private String nameDeliveryDetailSite;

    // 받는 날짜
    private LocalDate orderDate;

    // 받는 시간
    private Long idxOrderTime;
    private LocalTime arrivalTime;
    private LocalTime additionalTime;

    List<OrderItemResponseDto> orderItems = new ArrayList<>();

    public static OrderResponseDto fromEntity(Order entity) {
        if(entity == null) return null;
        OrderResponseDto dto = new OrderResponseDto(); // new ModelMapper().map(entity, OrderResponseDto.class); -> cashReceiptType 매핑 오류

        // 기본 정보 (mapper 대체)
        dto.setIdx(entity.getIdx());
        dto.setRegisterDate(entity.getRegisterDate());
        dto.setModifyDate(entity.getModifyDate());

        // 주문 기본 정보
        PaymentInfo paymentInfo = entity.getPaymentInfo();
        dto.setOrderType(entity.getOrderType());
        dto.setBoxNumber(entity.getBoxNumber());
        dto.setPaymentType(paymentInfo.getPaymentType());
        dto.setOrdererType(entity.getOrderer().getOrdererType());
        if(entity.getOrderer().getUser() != null) {
            dto.setOrdererName(entity.getOrderer().getUser().getName());
            dto.setOrdererPn(entity.getOrderer().getUser().getPhoneNumber());
        } else {
            dto.setOrdererName("비회원");
        }
        dto.setNote(entity.getNote());
        dto.setPhoneNumber(entity.getOrderer().getPhoneNumber());

        // 결제 정보
        dto.setUsingPoint(paymentInfo.getUsingPoint());
        dto.setUsingCoupons(convertCoupons(paymentInfo.getUsingCoupons()));
        dto.setUsingPromotions(convertPromotions(paymentInfo.getUsingPromotions()));
        dto.setSavedPoint(paymentInfo.getSavedPoint());
        dto.setCashReceipt(paymentInfo.getCashReceipt());
        dto.setCashReceiptType(paymentInfo.getCashReceiptType());
        dto.setTotalCost(entity.totalCost());
        dto.setDiscountCost(entity.discountCost());
        dto.setPaymentCost(entity.paymentCost());

        // 받는 장소
        DeliveryDetailSite deliveryDetailSite = entity.getDeliveryDetailSite();
        DeliverySite deliverySite = deliveryDetailSite.getDeliverySite();
        dto.setIdxDeliverySite(deliverySite.getIdx());
        dto.setIdxDeliveryDetailSite(deliveryDetailSite.getIdx());
        dto.setNameDeliverySite(deliverySite.getName());
        dto.setNameDeliveryDetailSite(deliveryDetailSite.getName());

        // 받는 날짜
        dto.setOrderDate(entity.getOrderDate());

        // 받는 시간
        OrderTime orderTime = entity.getOrderTime();
        dto.setIdxOrderTime(orderTime.getIdx());
        dto.setArrivalTime(orderTime.getArrivalTime());
        dto.setAdditionalTime(deliveryDetailSite.getAdditionalTime());

        // 주문 내역
        dto.setOrderItems(convertOrderItems(entity.getOrderItems()));

        return dto;
    }

    public static List<OrderResponseDto> fromEntities(List<Order> entities) {
        if(entities == null) return null;
        List<OrderResponseDto> dtos = new ArrayList<>();
        for(Order entity : entities) {
            dtos.add(fromEntity(entity));
        }
        return dtos;
    }

    private static List<CouponDto> convertCoupons(List<CouponMapper> couponMappers) {
        List<CouponDto> couponDtos = new ArrayList<>();
        if(couponMappers != null) {
            for(CouponMapper couponMapper : couponMappers) {
                Coupon coupon = couponMapper.getCoupon();
                if(coupon != null) {
                    couponDtos.add(CouponDto.fromEntity(coupon));
                }
            }
        }
        return couponDtos;
    }

    private static List<PromotionDto> convertPromotions(List<PromotionMapper> promotionMappers) {
        List<PromotionDto> promotionDtos = new ArrayList<>();
        if(promotionMappers != null) {
            for(PromotionMapper promotionMapper : promotionMappers) {
                Promotion promotion = promotionMapper.getPromotion();
                if(promotion != null) {
                    promotionDtos.add(PromotionDto.fromEntity(promotion));
                }
            }
        }
        return promotionDtos;
    }


    private static List<OrderItemResponseDto> convertOrderItems(List<OrderItem> orderItems) {
        List<OrderItemResponseDto> orderItemResponseDtos = new ArrayList<>();
        if(orderItems != null) {
            for(OrderItem orderItem : orderItems) {
                if(orderItem != null) {
                    orderItemResponseDtos.add(OrderItemResponseDto.fromEntity(orderItem));
                }
            }
        }
        return orderItemResponseDtos;
    }

}

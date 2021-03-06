package kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.service.impl;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.dao.jpa.OrderJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.ordertime.mapper.dao.jpa.OrderTimeMapperJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.ordertime.ordertime.dao.jpa.OrderTimeJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.like.dao.jpa.StoreLikeJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.dao.jpa.StoreJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.dto.StoreDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.dto.StoreQuantityOrderableDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.dto.StoreSummaryDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.exception.StoreException;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.SortType;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.Store;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.model.info.ProductionInfo;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.store.service.StoreService;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.dao.jpa.UserJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StoreServiceImpl implements StoreService {

    StoreJpaRepository storeRepo;
    OrderJpaRepository orderRepo;
    OrderTimeJpaRepository orderTimeRepo;
    OrderTimeMapperJpaRepository orderTimeMapperRepo;
    StoreLikeJpaRepository storeLikeRepo;
    UserJpaRepository userRepo;

    @Override
    public List<StoreDto> findByIdxDeliverySite(Long dIdx, Pageable pageable) {
        List<Store> stores = storeRepo.findByIdxDeliverySite(dIdx, pageable);
        return StoreDto.fromEntities(stores);
    }

    @Override
    public StoreDto findByIdx(Long idx, String phoneNumber) {
        Store entity = storeRepo.findByIdxAndIsActiveIsTrue(idx);
        StoreDto dto = StoreDto.fromEntity(entity);
        boolean isLike = false;
        if(phoneNumber != null) {
            Long uIdx = userRepo.findIdxByPhoneNumberAndIsActiveIsTrue(phoneNumber);
            isLike = storeLikeRepo.existsByIdxUserAndIdxStore(uIdx, idx);
        }
        dto.setIsLike(isLike);
        return dto;
    }

    @Override
    public long count() {
        return storeRepo.countByIsActiveIsTrue();
    }

    @Override
    public List<StoreSummaryDto> findOpeningStores(Long dIdx, Long oIdx, LocalDate oDate, Pageable pageable, SortType sortType) {
        List<StoreSummaryDto> summaries = new ArrayList<>();

        LocalDateTime orderEndTime = LocalDateTime.of(oDate, _orderEndTime(oIdx));
        LocalDateTime now = LocalDateTime.now();

        if(now.isBefore(orderEndTime)) {
            // int dMinute = (int) Duration.between(now, orderEndTime).toMinutes();  // ?????? ???????????? ?????? ??????
            for(Store store : _orderableStores(dIdx, oIdx, pageable, sortType)) {
                StoreSummaryDto dto = StoreSummaryDto.fromEntity(store);
                // dto.setQuantityOrderable(qo(store.getProductionInfo(), dMinute, aov(dIdx, store.getIdx(), oIdx, oDate)));
                dto.setQuantityOrderable(qo2(store.getProductionInfo(), aov(dIdx, store.getIdx(), oIdx, oDate)));
                summaries.add(dto);
            }
        }
        return summaries;
    }

    @Override
    public List<StoreQuantityOrderableDto> findQuantityOrderableByIdxes(Long dIdx, Long oIdx, LocalDate oDate, List<Long> sIdxes) {
        List<StoreQuantityOrderableDto> quantities = new ArrayList<>();

        // int dMinute = (int) Duration.between(LocalDateTime.now(), LocalDateTime.of(oDate, _orderEndTime(oIdx))).toMinutes(); // ?????? ???????????? ?????? ??????
        for(Store store : storeRepo.findAllById(sIdxes)) {
            if(orderTimeMapperRepo.existsByOrderTime_IdxAndStore_Idx(oIdx, store.getIdx())) {
                quantities.add(StoreQuantityOrderableDto.builder()
                        .idx(store.getIdx())
                        // .quantityOrderable(qo(store.getProductionInfo(), dMinute, aov(dIdx, store.getIdx(), oIdx, oDate)))
                        .quantityOrderable(qo2(store.getProductionInfo(), aov(dIdx, store.getIdx(), oIdx, oDate)))
                        .build());
            }
        }
        return quantities;
    }

    @Override
    public long countOpeningStores(Long dIdx, Long oIdx, LocalDate oDate) {
        LocalDateTime orderEndTime = LocalDateTime.of(oDate, _orderEndTime(oIdx));
        LocalDateTime now = LocalDateTime.now();

        if(now.isBefore(orderEndTime)) {
            return storeRepo.countByIdxOrderTimeAndIdxDeliverySiteAndIsActiveIsTrue(oIdx, dIdx);
        } else {
            return 0L;
        }
    }

    private int qo2(ProductionInfo info, int aov) {
        int max = info.getMaximumProduction();       // ?????? ?????? ?????? ??????
        return max - aov < 0 ? 0 : max - aov;
    }

    /**
     * ?????? ?????? ??????
     * quantityOrderable
     */
    private int qo(ProductionInfo info, int dMinute, int aov) {
        int pp = info.getParallelProduction();       // ?????? ?????? ?????????
        int mt = info.getMinimumTime();              // ?????? ?????? ?????? ??????
        int max = info.getMaximumProduction();       // ?????? ?????? ?????? ??????
        int avp = (pp / mt) * dMinute;                 // ?????? ?????? ??????
        avp = avp >= max ? max : avp;

        return avp - aov < 0 ? 0 : avp - aov;
    }

    /**
     * ?????? ?????????
     * available order volume
     */
    private int aov(Long dIdx, Long sIdx, Long oIdx, LocalDate oDate) {
        return orderRepo.accumulatedOrderVolume(dIdx, sIdx, oIdx, oDate);
    }

    private LocalTime _orderEndTime(Long oIdx) {
        return orderTimeRepo.findById(oIdx)
                .orElseThrow(() -> new StoreException("invalid orderTime."))
                .getOrderEndTime();
    }

    private List<Store> _orderableStores(Long dIdx, Long oIdx, Pageable pageable, SortType sortType) {

        return storeRepo // (????????????, ????????????)??? ???????????? ?????????
            .findStoreByIdxOrderTimeAndIdxDeliverySiteAndIsActiveIsTrue(oIdx, dIdx, pageable, sortType)
            .getContent();
    }
}

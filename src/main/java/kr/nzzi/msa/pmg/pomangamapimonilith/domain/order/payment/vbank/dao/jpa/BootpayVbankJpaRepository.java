package kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.payment.vbank.dao.jpa;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.order.model.bootpay.BootpayVbank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(exported = false)
public interface BootpayVbankJpaRepository extends JpaRepository<BootpayVbank, Long>, BootpayVbankCustomRepository {
    BootpayVbank findByIdxOrderAndIsActiveIsTrue(Long oIdx);
}

interface BootpayVbankCustomRepository {

}

@Transactional(readOnly = true)
class BootpayVbankCustomRepositoryImpl extends QuerydslRepositorySupport implements BootpayVbankCustomRepository {

    public BootpayVbankCustomRepositoryImpl() {
        super(BootpayVbank.class);
    }


}
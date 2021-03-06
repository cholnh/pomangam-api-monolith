package kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.ordertime.mapper.dao.jpa;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.order.ordertime.mapper.model.OrderTimeMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;


@RepositoryRestResource(exported = false)
public interface OrderTimeMapperJpaRepository extends JpaRepository<OrderTimeMapper, Long>, OrderTimeMapperCustomRepository {
    boolean existsByOrderTime_IdxAndStore_Idx(Long oIdx, Long sIdx);
}

interface OrderTimeMapperCustomRepository {

}

@Transactional(readOnly = true)
class OrderTimeMapperCustomRepositoryImpl extends QuerydslRepositorySupport implements OrderTimeMapperCustomRepository {

    public OrderTimeMapperCustomRepositoryImpl() {
        super(OrderTimeMapper.class);
    }

}

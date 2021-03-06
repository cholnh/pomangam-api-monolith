package kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.point.log.dao.jpa;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.marketing.point.log.model.PointLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RepositoryRestResource(exported = false)
public interface PointLogJpaRepository extends JpaRepository<PointLog, Long>, PointLogCustomRepository {
    List<PointLog> findByIdxUser(Long uIdx);
    Page<PointLog> findByIdxUser(Long uIdx, Pageable pageable);
}

interface PointLogCustomRepository {

}

@Transactional(readOnly = true)
class PointLogCustomRepositoryImpl extends QuerydslRepositorySupport implements PointLogCustomRepository {

    public PointLogCustomRepositoryImpl() {
        super(PointLog.class);
    }


}

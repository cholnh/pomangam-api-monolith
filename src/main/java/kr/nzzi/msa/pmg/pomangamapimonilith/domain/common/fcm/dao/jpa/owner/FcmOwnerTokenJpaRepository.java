package kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.fcm.dao.jpa.owner;

import com.querydsl.jpa.JPAExpressions;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.fcm.model.owner.FcmOwnerToken;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.fcm.model.owner.QFcmOwnerToken;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.owner.model.QOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RepositoryRestResource(exported = false)
public interface FcmOwnerTokenJpaRepository extends JpaRepository<FcmOwnerToken, Long>, FcmOwnerTokenCustomRepository {
    FcmOwnerToken findByIdxAndIsActiveIsTrue(Long idx);
    FcmOwnerToken findByToken(String token);
}

interface FcmOwnerTokenCustomRepository {
    List<FcmOwnerToken> findByStore(Long idxStore);
}

@Transactional(readOnly = true)
class FcmOwnerTokenCustomRepositoryImpl extends QuerydslRepositorySupport implements FcmOwnerTokenCustomRepository {

    public FcmOwnerTokenCustomRepositoryImpl() {
        super(FcmOwnerToken.class);
    }

    public List<FcmOwnerToken> findByStore(Long idxStore) {
        QFcmOwnerToken fcmOwnerToken = QFcmOwnerToken.fcmOwnerToken;
        QOwner owner = QOwner.owner;

        return from(fcmOwnerToken)
                .select(fcmOwnerToken)
                .where(fcmOwnerToken.idx.in(
                        JPAExpressions
                            .select(owner.idxFcmToken)
                            .from(owner)
                            .where(owner.idxStore.eq(idxStore)
                                .and(owner.isActive.isTrue()))
                ).and(fcmOwnerToken.isActive.isTrue()))
                .fetch();
    }
}

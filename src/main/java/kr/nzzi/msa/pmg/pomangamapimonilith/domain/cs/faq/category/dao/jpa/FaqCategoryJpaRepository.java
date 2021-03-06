package kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.faq.category.dao.jpa;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.faq.category.model.FaqCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;


@RepositoryRestResource(exported = false)
public interface FaqCategoryJpaRepository extends JpaRepository<FaqCategory, Long>, FaqCategoryCustomRepository {

}

interface FaqCategoryCustomRepository {

}

@Transactional(readOnly = true)
class FaqCategoryCustomRepositoryImpl extends QuerydslRepositorySupport implements FaqCategoryCustomRepository {

    public FaqCategoryCustomRepositoryImpl() {
        super(FaqCategory.class);
    }


}

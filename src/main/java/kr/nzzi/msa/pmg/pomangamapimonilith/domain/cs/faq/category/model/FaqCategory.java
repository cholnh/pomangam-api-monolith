package kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.faq.category.model;

import kr.nzzi.msa.pmg.pomangamapimonilith.global._base.Auditable;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.faq.faq.model.Faq;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "faq_category_tbl")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class FaqCategory extends Auditable {

    /**
     * 카테고리 제목
     * 글자수: utf8 기준 / 영문 100자 / 한글 100자
     */
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * faq Mapper
     */
    @JoinColumn(name = "idx_faq_category", nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Faq> faqs = new ArrayList<>();

    @Builder
    public FaqCategory(Long idx, String title, List<Faq> faqs) {
        super.setIdx(idx);
        this.title = title;
        this.faqs = faqs;
    }

    public void addFaq(Faq faq) {
        if(this.faqs == null) {
            this.faqs = new ArrayList<>();
        }
        this.faqs.add(faq);
    }
}


package kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.faq.faq.model;

import kr.nzzi.msa.pmg.pomangamapimonilith.global._base.Auditable;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "faq_tbl")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Faq extends Auditable {

    /**
     * 자주하는질문 제목
     * 글자수: utf8 기준 / 영문 100자 / 한글 100자
     */
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * 자주하는질문 내용 (webView html)
     * TEXT: 65535 Byte (64KB) / utf8 기준(3바이트 문자)으로 21844 글자 저장가능
     */
    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Builder
    public Faq(Long idx, String title, String contents) {
        super.setIdx(idx);
        this.title = title;
        this.contents = contents;
    }
}
package kr.nzzi.msa.pmg.pomangamapimonilith.global._base;

import kr.nzzi.msa.pmg.pomangamapimonilith.global.annotation.BooleanToYNConverter;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@EntityListeners(value = AuditingEntityListener.class)
@MappedSuperclass
@Data
public abstract class Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    /**
     * 활성화 여부 (Y/N)
     * default: true(Y)
     * 대문자 필수
     */
    @Column(name = "is_active", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'Y'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isActive;

    /**
     * 등록 날짜
     */
    @Column(name = "register_date", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime registerDate;

    /**
     * 수정 날짜
     */
    @Column(name = "modify_date", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime modifyDate;
}

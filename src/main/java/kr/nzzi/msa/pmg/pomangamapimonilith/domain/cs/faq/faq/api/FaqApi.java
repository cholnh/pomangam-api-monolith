package kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.faq.faq.api;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.cs.faq.faq.service.impl.FaqServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dsites/{dIdx}/faqs")
@AllArgsConstructor
public class FaqApi {

    private FaqServiceImpl faqService;

    @GetMapping
    public ResponseEntity<?> findByIdxDeliverySite(
            @PathVariable(value = "dIdx", required = true) Long dIdx,
            @PageableDefault(sort = {"idx"}, direction = Sort.Direction.DESC, page = 0, size = 10) Pageable pageable
    ) {
        return new ResponseEntity(faqService.findByIdxDeliverySite(dIdx, pageable), HttpStatus.OK);
    }
}
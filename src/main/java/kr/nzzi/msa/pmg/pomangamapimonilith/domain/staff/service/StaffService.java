package kr.nzzi.msa.pmg.pomangamapimonilith.domain.staff.service;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.staff.dto.StaffDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.staff.model.Staff;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StaffService {

    StaffDto findById(String id);

    Long findIdxById(String id);

    List<StaffDto> findAll();

    List<StaffDto> findAll(Pageable pageable);

    StaffDto saveStaff(Staff Staff);

    StaffDto updateStaffPassword(String id, String password);

    Boolean isExistById(String id);

    StaffDto patchStaff(String id, Staff Staff);

    Boolean deleteStaff(String id);
}

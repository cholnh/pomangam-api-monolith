package kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.owner.service.impl;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.owner.dao.jpa.OwnerJpaRepository;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.owner.dto.OwnerDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.owner.model.Owner;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.store.owner.service.OwnerService;
import kr.nzzi.msa.pmg.pomangamapimonilith.global.util.formatter.PhoneNumberFormatter;
import kr.nzzi.msa.pmg.pomangamapimonilith.global.util.reflection.ReflectionUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    PasswordEncoder passwordEncoder;
    OwnerJpaRepository ownerRepo;

    @Override
    public OwnerDto findById(String id) {
        Owner owner = ownerRepo.findByIdAndIsActiveIsTrue(id);
        if(owner == null) {
            return null;
        }
        OwnerDto ownerDto = OwnerDto.fromEntity(owner);
        return ownerDto;
    }

    @Override
    public Long findIdxById(String id) {
        return ownerRepo.findIdxByIdAndIsActiveIsTrue(id);
    }

    @Override
    public List<OwnerDto> findAll() {
        return OwnerDto.fromEntities(ownerRepo.findAll());
    }

    @Override
    public List<OwnerDto> findAll(Pageable pageable) {
        return OwnerDto.fromEntities(ownerRepo.findAll(pageable).getContent());
    }

    @Override
    @Transactional
    public OwnerDto saveOwner(Owner owner) {
        owner.getPassword().setFailedCount(0);
        owner.getPassword().setPasswordValue(passwordEncoder.encode(owner.getPassword().getPasswordValue()));
        owner.setPhoneNumber(PhoneNumberFormatter.format(owner.getPhoneNumber()));

        OwnerDto dto = OwnerDto.fromEntity(ownerRepo.save(owner));

        return dto;
    }

    @Override
    public Boolean isExistById(String id) {
        if(id != null) {
            return ownerRepo.existsById(id);
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public OwnerDto updateOwnerPassword(String id, String password) {
        final Owner fetched = ownerRepo.findByIdAndIsActiveIsTrue(id);
        if (fetched == null) {
            return null;
        }
        fetched.getPassword().setPasswordValue(passwordEncoder.encode(password));
        fetched.setModifyDate(LocalDateTime.now());

        ownerRepo.save(fetched);

        return OwnerDto.fromEntity(fetched);
    }

    @Override
    @Transactional
    public OwnerDto patchOwner(String id, Owner owner) {
        final Owner fetched = ownerRepo.findByIdAndIsActiveIsTrue(id);
        if(fetched == null) {
            return null;
        }

        try {
            ReflectionUtils.oldInstanceByNewInstance(fetched, owner);
            ownerRepo.save(fetched);

            OwnerDto dto = OwnerDto.fromEntity(fetched);
            return OwnerDto.fromEntity(fetched);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public Boolean deleteOwner(String id) {
        final Owner fetched = ownerRepo.findByIdAndIsActiveIsTrue(id);
        if (fetched == null) {
            return false;
        } else {
            ownerRepo.delete(fetched);
            return true;
        }
    }
}

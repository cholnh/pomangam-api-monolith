package kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Service;

@Service
public class PreUserDetailsChecker implements UserDetailsChecker {

    @Autowired
    public PreUserDetailsChecker() {
    }

    @Override
    public void check(UserDetails toCheck) {
    }
}

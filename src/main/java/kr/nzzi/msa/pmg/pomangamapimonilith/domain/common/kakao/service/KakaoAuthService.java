package kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.kakao.service;

import org.springframework.http.ResponseEntity;

public interface KakaoAuthService {
    String getAuthCode();
    ResponseEntity<?> sendAuthCode(String phone_number, String auth_code);

    boolean checkAbusing(String ip);
    void saveAuthCode(String phone_number, String auth_code);
    boolean checkAuthCode(String phone_number, String auth_code);
    boolean checkAuthCodeNotDelete(String phone_number, String auth_code);
}

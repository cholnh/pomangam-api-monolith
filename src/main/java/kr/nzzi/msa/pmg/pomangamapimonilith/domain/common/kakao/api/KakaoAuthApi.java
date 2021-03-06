package kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.kakao.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.kakao.dto.PhoneNumber;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.kakao.dto.UpdateInputDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.kakao.service.impl.KakaoAuthServiceImpl;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.kakao.service.impl.KakaoOauthServiceImpl;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.dto.UserDto;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.user.service.impl.UserServiceImpl;
import kr.nzzi.msa.pmg.pomangamapimonilith.global.util.formatter.PhoneNumberFormatter;
import kr.nzzi.msa.pmg.pomangamapimonilith.global.util.security.Ip;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class KakaoAuthApi {

    KakaoAuthServiceImpl kakaoAuthService;
    UserServiceImpl userService;
    KakaoOauthServiceImpl kakaoOauthService;

    @GetMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "pn") String pn
    ) {
        return new ResponseEntity(kakaoOauthService.verifyOauthLogin(pn, token), HttpStatus.OK);
    }

    @PostMapping("/code/join")
    public ResponseEntity<?> generateAuthCodeForJoin(@RequestBody PhoneNumber phone) {
        String phoneNumber = phone.getPhoneNumber();
        phoneNumber = PhoneNumberFormatter.format(phoneNumber);
        if(userService.isExistByPhone(phoneNumber)) {
            return new ResponseEntity("User already registered ", HttpStatus.BAD_REQUEST);
        }
        return generateAuthCode(phone);
    }

    @PostMapping("/code/id")
    public ResponseEntity<?> generateAuthCode(@RequestBody PhoneNumber phone) {
        String phoneNumber = phone.getPhoneNumber();
        if(kakaoAuthService.checkAbusing(Ip.getInfo())) {
            return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
        }
        phoneNumber = PhoneNumberFormatter.format(phoneNumber);

        String auth_code = kakaoAuthService.getAuthCode();
        ResponseEntity<?> sendResult = kakaoAuthService.sendAuthCode(phoneNumber, auth_code);

        List<ApiResultBean> bean = new Gson().fromJson(sendResult.getBody()+"", new TypeToken<List<ApiResultBean>>() {}.getType());
        if(bean.get(0).getCode().equals("success")) {
            // success
            //AuthCode result = new AuthCode(auth_code, CustomTime.curDateTime());
            kakaoAuthService.saveAuthCode(phoneNumber, auth_code);
            return new ResponseEntity(bean.get(0), HttpStatus.OK);
        } else {
            // fail
            return new ResponseEntity(bean.get(0), sendResult.getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/code/pw")
    public ResponseEntity<?> generateAuthCodeForPw(@RequestBody PhoneNumber phone) {
        String phoneNumber = phone.getPhoneNumber();
        phoneNumber = PhoneNumberFormatter.format(phoneNumber);
        if(!userService.isExistByPhone(phoneNumber)) {
            return new ResponseEntity("User not found", HttpStatus.BAD_REQUEST);
        }
        return generateAuthCode(phone);
    }

    @PostMapping("/check/id")
    public ResponseEntity<?> checkAuthCodeForId(@RequestBody PhoneNumber phone) {
        String phoneNumber = phone.getPhoneNumber();
        String authCode = phone.getCode();
        phoneNumber = PhoneNumberFormatter.format(phoneNumber);
        if(kakaoAuthService.checkAuthCodeNotDelete(phoneNumber, authCode)) {
            UserDto user = userService.findByPhoneNumber(phoneNumber);
            if(user == null) {
                return new ResponseEntity(false, HttpStatus.OK);
            } else {
                user.setPassword(null);
                return new ResponseEntity(true, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity("INVALID CODE", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/check/join")
    public ResponseEntity<?> checkAuthCode(@RequestBody PhoneNumber phone) {
        String phoneNumber = phone.getPhoneNumber();
        String authCode = phone.getCode();
        phoneNumber = PhoneNumberFormatter.format(phoneNumber);
        boolean isValidAuthCode = kakaoAuthService.checkAuthCode(phoneNumber, authCode);

        if(isValidAuthCode) {
            return new ResponseEntity(isValidAuthCode, HttpStatus.OK);
        } else {
            return new ResponseEntity(isValidAuthCode, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/check/pw")
    public ResponseEntity<?> checkAuthCodeForPw(@RequestBody PhoneNumber phone) {
        String phoneNumber = phone.getPhoneNumber();
        String authCode = phone.getCode();
        phoneNumber = PhoneNumberFormatter.format(phoneNumber);
        return new ResponseEntity(kakaoAuthService.checkAuthCodeNotDelete(phoneNumber, authCode), HttpStatus.OK);
    }

    @PostMapping("/update/pw")
    public ResponseEntity<?> updatePw(@RequestBody UpdateInputDto dto) {
        String phoneNumber = dto.getPhoneNumber();
        String authCode = dto.getCode();
        phoneNumber = PhoneNumberFormatter.format(phoneNumber);

        if(kakaoAuthService.checkAuthCode(phoneNumber, authCode)) {
            if(userService.isExistByPhone(phoneNumber)) {
                UserDto userDto = userService.updateUserPassword(phoneNumber, dto.getPassword());
                userDto.setPassword(null);
                return new ResponseEntity(userDto, HttpStatus.OK);
            } else {
                return new ResponseEntity("USER NOT EXIST", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity("INVALID CODE", HttpStatus.BAD_REQUEST);
        }
    }

    @Data
    @AllArgsConstructor
    public class ApiResultBean {
        String code;
        Data data;
        String message;

        @lombok.Data
        @AllArgsConstructor
        class Data {
            String msgid;
        }
    }

    @Data
    @AllArgsConstructor
    class AuthCode {
        String auth_code;
        String register_date;
    }
}

package kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.oauth.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * org.springframework.dao.DuplicateKeyException: PreparedStatementCallback 의 이슈 해결
 * oauth_access_token의 authentication_id (PK) 필드에서 중복 삽입 이슈
 * 네트워크 Heavy load 시 문제가 간헐적으로 발생
 * AOP 통해, 중복키 발생 시 지연 후 실행 시키는 방식으로 해결
 */

@Aspect
@Component
public class TokenEndpointRetryInterceptor {

    private static final int MAX_RETRIES = 4;

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.*(..))")
    public Object execute (ProceedingJoinPoint aJoinPoint) throws Throwable {
        int tts = 1000;
        for (int i=0; i<MAX_RETRIES; i++) {
            try {
                return aJoinPoint.proceed();
            } catch (DuplicateKeyException e) {
                Thread.sleep(tts);
                tts=tts*2;
            }
        }
        throw new IllegalStateException("Could not execute: " + aJoinPoint.getSignature().getName());
    }
}
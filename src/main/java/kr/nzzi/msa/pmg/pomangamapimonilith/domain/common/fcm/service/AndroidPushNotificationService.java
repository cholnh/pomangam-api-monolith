package kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.fcm.service;

import org.springframework.http.HttpEntity;

import java.util.concurrent.CompletableFuture;

public interface AndroidPushNotificationService {
    CompletableFuture<String> send(HttpEntity<String> entity);
}

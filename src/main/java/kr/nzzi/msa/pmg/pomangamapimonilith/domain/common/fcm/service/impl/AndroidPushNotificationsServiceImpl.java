package kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.fcm.service.impl;

import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.fcm.interceptor.HeaderRequestInterceptor;
import kr.nzzi.msa.pmg.pomangamapimonilith.domain.common.fcm.service.AndroidPushNotificationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class AndroidPushNotificationsServiceImpl implements AndroidPushNotificationService {
    private static final String FIREBASE_SERVER_KEY = "AAAAYVUy7Y0:APA91bH015oSaxj0D2W7Kl7rvffDtZttbmoKr1JKT6xUVlRhKByoyLomkWUvBcbag2YBIJi62rvghbprG8bM6GteYuszm3R4ZPOccrJ3UMA7lMlv5JBqrlvMH5li4O-Zh3px6jCVu-eN";
    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";

    @Async
    @Override
    public CompletableFuture<String> send(HttpEntity<String> entity) {
        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json; charset=UTF-8" ));
        restTemplate.setInterceptors(interceptors);

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);

        return CompletableFuture.completedFuture(firebaseResponse);
    }
}

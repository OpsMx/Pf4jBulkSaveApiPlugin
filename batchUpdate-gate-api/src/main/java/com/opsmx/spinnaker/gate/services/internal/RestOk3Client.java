package com.opsmx.spinnaker.gate.services.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.retrofit.Ok3Client;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

import java.util.concurrent.TimeUnit;

public class RestOk3Client {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public Front50Api getClient(long timeoutInSecs, String front50Url) {

        log.debug(" RestOk3Client getClient: start");
        log.info(" timeout in seconds : " + timeoutInSecs);
        log.info(" front50Url : " + front50Url);
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new JavaTimeModule());
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient = okHttpClient.newBuilder().readTimeout(timeoutInSecs, TimeUnit.SECONDS)
                .writeTimeout(timeoutInSecs, TimeUnit.SECONDS)
                .connectTimeout(timeoutInSecs, TimeUnit.SECONDS).build();
        log.debug(" RestOk3Client getClient: end");
        return new RestAdapter.Builder()
                .setEndpoint(front50Url)
                .setClient(new Ok3Client(okHttpClient))
                .setConverter(new JacksonConverter(objectMapper))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build()
                .create(Front50Api.class);
    }
}

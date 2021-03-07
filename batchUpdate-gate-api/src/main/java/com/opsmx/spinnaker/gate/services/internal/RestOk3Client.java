package com.opsmx.spinnaker.gate.services.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.retrofit.Ok3Client;
import com.opsmx.spinnaker.gate.plugins.GateBulkSaveApiExtension;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

import java.util.concurrent.TimeUnit;

public class RestOk3Client {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private GateBulkSaveApiExtension.ApiExtensionConfigProperties configProps
            = new GateBulkSaveApiExtension.ApiExtensionConfigProperties();

    public Front50Api getClient() {

        log.debug(" RestOk3Client getClient: start");
        log.info(" timeout in seconds : " + configProps.getTimeout());
        log.info(" front50Url : " + configProps.getFront50Url());
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new JavaTimeModule());
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient = okHttpClient.newBuilder().readTimeout(configProps.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(configProps.getTimeout(), TimeUnit.SECONDS)
                .connectTimeout(configProps.getTimeout(), TimeUnit.SECONDS).build();
        log.debug(" RestOk3Client getClient: end");
        return new RestAdapter.Builder()
                .setEndpoint(configProps.getFront50Url())
                .setClient(new Ok3Client(okHttpClient))
                .setConverter(new JacksonConverter(objectMapper))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build()
                .create(Front50Api.class);
    }
}

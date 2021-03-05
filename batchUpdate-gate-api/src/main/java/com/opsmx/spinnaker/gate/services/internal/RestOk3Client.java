package com.opsmx.spinnaker.gate.services.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.retrofit.Ok3Client;
import com.opsmx.spinnaker.gate.util.PropertiesReader;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RestOk3Client {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static long timeoutInSecs = 11;

    private static String front50Url = "http://localhost:8081";

    public Front50Api getClient() {

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

    static {
        try {
            PropertiesReader propertiesReader = new PropertiesReader("batchUpdate.properties");
            timeoutInSecs = Long.parseLong(propertiesReader.getProperty("okhttp.timeout.in.secs"));
            front50Url = propertiesReader.getProperty("front50.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

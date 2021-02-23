package com.opsmx.spinnaker.gate.services.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jakewharton.retrofit.Ok3Client;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class RestOk3Client {

    public Front50Api getClient() {
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new JavaTimeModule());
        return new RestAdapter.Builder()
                .setEndpoint("http://localhost:8080")
                .setClient(new Ok3Client())
                .setConverter(new JacksonConverter(objectMapper))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build()
                .create(Front50Api.class);
    }
}

package com.opsmx.spinnaker.gate.services.internal;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

import java.util.List;
import java.util.Map;

public interface Front50Api {

    @POST("/pipelines/batchUpdate")
    Response savePipelines(@Body List<Map<String, Object>> pipelines);
}

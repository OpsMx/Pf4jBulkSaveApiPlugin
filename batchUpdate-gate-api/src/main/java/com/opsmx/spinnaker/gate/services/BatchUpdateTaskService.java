package com.opsmx.spinnaker.gate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.gate.services.internal.Front50Service;
import com.netflix.spinnaker.security.AuthenticatedRequest;
import com.opsmx.spinnaker.gate.services.internal.Front50Api;
import com.opsmx.spinnaker.gate.services.internal.RestOk3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BatchUpdateTaskService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private Front50Api front50Api = new RestOk3Client().getClient();

    ObjectMapper objectMapper = new ObjectMapper();

    public BatchUpdateTaskService(Front50Api front50Api) {
        this.front50Api = front50Api;
    }

    public Map bulkCreateAndWaitForCompletion(Map body) {
        return bulkCreateAndWaitForCompletion(body, 300, 1000);
    }

    public Map create(Map body) {

        log.debug(" create() method start : " );
        if (body.containsKey("application")) {
            AuthenticatedRequest.setApplication(body.get("application").toString());
        }
        List<Map<String, Object>> pipelineList = new ArrayList<>();
        pipelineList = (List) body.get("pipeline");
        Response response = front50Api.savePipelines(pipelineList);
        Map<String, Object> outputs = new HashMap<>();
        try {
            Map<String, Object> result =
                    (Map<String, Object>) objectMapper.readValue(response.getBody().in(), Map.class);
            outputs.put("result", result);
        } catch (Exception e) {
            log.error("Unable to deserialize saved pipeline, reason: ", e.getMessage());
        }
        log.debug(" create() method end : " );
        return outputs;
    }

    public Map bulkCreateAndWaitForCompletion(Map body, int maxPolls, int intervalMs) {

        log.debug("Bulk Creating and waiting for completion: start");
        if (body.containsKey("application")) {
            AuthenticatedRequest.setApplication(body.get("application").toString());
        }
        Map createResult = create(body);
        log.debug("Bulk Creating and waiting for completion: end");
        return createResult;
    }
}

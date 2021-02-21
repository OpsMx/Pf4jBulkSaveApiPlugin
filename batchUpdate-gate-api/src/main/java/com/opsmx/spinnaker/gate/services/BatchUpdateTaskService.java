package com.opsmx.spinnaker.gate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.gate.services.internal.Front50Service;
import com.netflix.spinnaker.security.AuthenticatedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit.client.Response;

import java.util.*;

@Service
public class BatchUpdateTaskService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private Front50Service front50Service;

    ObjectMapper objectMapper = new ObjectMapper();

    public BatchUpdateTaskService(Front50Service front50Service) {
        this.front50Service = front50Service;
    }

    public Map bulkCreateAndWaitForCompletion(Map body) {
        return bulkCreateAndWaitForCompletion(body, 300, 1000);
    }

    public Map create(Map body) {
        if (body.containsKey("application")) {
            AuthenticatedRequest.setApplication(body.get("application").toString());
        }
        List<Map<String, Object>> pipelineList = new ArrayList<>();
        pipelineList = (List) body.get("pipeline");
        Response response = front50Service.savePipelines(pipelineList);
        Map<String, Object> outputs = new HashMap<>();
        try {
            Map<String, Object> savedPipeline =
                    (Map<String, Object>) objectMapper.readValue(response.getBody().in(), Map.class);
            outputs.put("bulksave", savedPipeline);
        } catch (Exception e) {
            log.error("Unable to deserialize saved pipeline, reason: ", e.getMessage());
        }
        return outputs;
    }

    public Map bulkCreateAndWaitForCompletion(Map body, int maxPolls, int intervalMs) {

        log.info("Bulk Creating and waiting for completion: " + body);
        if (body.containsKey("application")) {
            AuthenticatedRequest.setApplication(body.get("application").toString());
        }
        Map createResult = create(body);
        return createResult;
    }
}

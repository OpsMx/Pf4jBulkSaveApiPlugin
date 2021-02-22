package com.opsmx.spinnaker.gate.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.gate.api.extension.ApiExtension;
import com.netflix.spinnaker.gate.api.extension.HttpRequest;
import com.netflix.spinnaker.gate.api.extension.HttpResponse;
import com.netflix.spinnaker.gate.services.internal.Front50Service;
import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration;
import com.netflix.spinnaker.kork.plugins.api.internal.ExtensionPointMetadataProvider;
import com.netflix.spinnaker.kork.plugins.api.internal.SpinnakerExtensionPoint;
import com.netflix.spinnaker.security.AuthenticatedRequest;
import com.opsmx.spinnaker.gate.services.BatchUpdateTaskService;
import org.jetbrains.annotations.NotNull;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Extension
public class GateBulkSaveApiExtension implements ApiExtension {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Front50Service front50Service;

    BatchUpdateTaskService batchUpdateTaskService = new BatchUpdateTaskService(front50Service);

    public GateBulkSaveApiExtension(ObjectMapper objectMapper,
                                    Front50Service front50Service) {

        this.objectMapper = objectMapper;
        this.front50Service = front50Service;
    }

    @NotNull
    @Override
    public String id() {
        return ApiExtensionConfigProperties.getId();
    }

    @Override
    public boolean handles(@NotNull HttpRequest httpRequest) {
        return supportedGet(httpRequest) || supportedPost(httpRequest);
    }

    @NotNull
    @Override
    public HttpResponse handle(@NotNull HttpRequest httpRequest) {
        if (supportedPost(httpRequest)) {
            return batchUpdateApiPipeline(httpRequest.getBody());
        }
        return echo(httpRequest);
    }

    @Override
    public Class<? extends SpinnakerExtensionPoint> getExtensionClass() {
        return ExtensionPointMetadataProvider.getExtensionClass(this);
    }

    private Boolean supportedGet(@Nonnull  HttpRequest httpRequest)  {
        return httpRequest.getMethod().equalsIgnoreCase("GET") &&
                httpRequest.getRequestURI().endsWith("");
    }

    private Boolean supportedPost(@Nonnull HttpRequest httpRequest) {
        return (httpRequest.getMethod().equalsIgnoreCase("POST")
                || httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) &&
                httpRequest.getRequestURI().endsWith("/batchUpdate");
    }

    private HttpResponse get() {
        return HttpResponse.of(204, emptyMap(), null);
    }

    private HttpResponse post(Map<String, Object> body) {
        return HttpResponse.of(200, emptyMap(), body);
    }

    private HttpResponse echo(@Nonnull HttpRequest httpRequest) {
        Map<String, String> echo = httpRequest.getParameters();
        return HttpResponse.of(200, emptyMap(), echo);
    }

    @PluginConfiguration("api-extension")
    static class ApiExtensionConfigProperties {

        private static String id;

        static String getId() {
            return id;
        }

        void setId(String id) {
            this.id = id;
        }
    }

    HttpResponse batchUpdateApiPipeline(String body) {

        log.info(" body : " + body);
        Map<String, Object> job = new HashMap<>();
        try {
            List<Map<String, Object>> pipelines =
                    (List<Map<String, Object>>) objectMapper.readValue(body, List.class);
            job.put("type", "savePipeline");
            job.put("user", AuthenticatedRequest.getSpinnakerUser().orElse("anonymous"));
            job.put("name","bulk save pipeline");
            job.put("application","bulk save application");
            job.put("pipeline", pipelines);
            log.info(" pipelines : " + pipelines);
        } catch (Exception e) {
            log.error("Unable to deserialize request body, reason: ", e.getMessage());
        }
        return post(batchUpdateTaskService.bulkCreateAndWaitForCompletion(job));
    }
}


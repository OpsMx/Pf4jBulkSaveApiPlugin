package com.opsmx.spinnaker.gate.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.gate.api.extension.ApiExtension;
import com.netflix.spinnaker.gate.api.extension.HttpRequest;
import com.netflix.spinnaker.gate.api.extension.HttpResponse;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Extension
@Configuration
@ComponentScan(basePackages = "com.opsmx.spinnaker.gate")
public class GateBulkSaveApiExtension implements ApiExtension {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    BatchUpdateTaskService batchUpdateTaskService;

    public GateBulkSaveApiExtension(ObjectMapper objectMapper,
                                    BatchUpdateTaskService batchUpdateTaskService) {
        this.objectMapper = objectMapper;
        this.batchUpdateTaskService = batchUpdateTaskService;
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

    private Boolean supportedGet(@Nonnull HttpRequest httpRequest) {
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
    public static class ApiExtensionConfigProperties {

        private static String id;

        private static long timeout;

        private static String front50Url;

        static String getId() {
            return id;
        }

        void setId(String id) {
            this.id = id;
        }

        public long getTimeout() {
            return timeout;
        }

        void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public String getFront50Url() {
            return front50Url;
        }

        void setFront50Url(String front50Url) {
            this.front50Url = front50Url;
        }
    }

    private HttpResponse batchUpdateApiPipeline(String body) {

        log.debug(" batchUpdateApiPipeline() method start : ");
        Map<String, Object> job = new HashMap<>();
        try {
            List<Map<String, Object>> pipelines =
                    (List<Map<String, Object>>) objectMapper.readValue(body, List.class);
            job.put("type", "savePipeline");
            job.put("user", AuthenticatedRequest.getSpinnakerUser().orElse("anonymous"));
            job.put("name", "bulk save pipeline");
            job.put("application", "bulk save application");
            job.put("pipeline", pipelines);
            log.info(" pipelines : " + pipelines);
        } catch (Exception e) {
            log.error("Unable to deserialize request body, reason: ", e.getMessage());
        }
        Map result = batchUpdateTaskService.bulkCreateAndWaitForCompletion(job);
        log.debug(" batchUpdateApiPipeline() method end : ");
        return post(result);
    }
}


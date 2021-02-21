package com.opsmx.spinnaker.gate.plugins;

import com.netflix.spinnaker.gate.api.extension.ApiExtension;
import com.netflix.spinnaker.gate.api.extension.HttpRequest;
import com.netflix.spinnaker.gate.api.extension.HttpResponse;
import com.netflix.spinnaker.kork.exceptions.HasAdditionalAttributes;
import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration;
import com.netflix.spinnaker.security.AuthenticatedRequest;
import groovy.transform.InheritConstructors;
import org.jetbrains.annotations.NotNull;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Extension
public class GateBulkSaveApiExtension implements ApiExtension {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    @Override
    public String id() {
        return ApiExtensionConfigProperties.getId();
    }

    @Override
    public boolean handles(@NotNull HttpRequest httpRequest) {
        return (supportedPost(httpRequest));
    }

    @NotNull
    @Override
    public HttpResponse handle(@NotNull HttpRequest httpRequest) {
        if (supportedPost(httpRequest)) {
            return batchUpdateApiPipeline(httpRequest.getBody());
        }
        return echo(httpRequest);
    }

    private Boolean supportedPost(@Nonnull HttpRequest httpRequest) {
        return httpRequest.getMethod().equalsIgnoreCase("PUT") &&
                httpRequest.getRequestURI().endsWith("/batchUpdate");
    }

    private HttpResponse get() {
        return  HttpResponse.of(204, emptyMap(), null);
    }

    private HttpResponse post(Map<String, Object> body) {
        return  HttpResponse.of(200, emptyMap(), body);
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @InheritConstructors
    class PipelineException extends RuntimeException implements HasAdditionalAttributes {
        Map<String, Object> additionalAttributes = new HashMap<>();

        PipelineException(String message) {
            super(message);
        }

        PipelineException(Map<String, Object> additionalAttributes) {
            this.additionalAttributes = additionalAttributes;
        }
    }

    HttpResponse batchUpdateApiPipeline(String body) {

        /*def job = [
        type       : "savePipeline",
                pipeline   : pipelineList,
                user       : AuthenticatedRequest.spinnakerUser.orElse("anonymous"),
                name       : "bulk save pipeline",
                application: "bulk save application"
        ]
        def result = bulkSaveTaskService.bulkCreateAndWaitForCompletion(job)*/
        log.info(" body : " + body);
        Map<String,Object> result = new HashMap<>();
        result.put("1","2");
        return post(result);
    }
}

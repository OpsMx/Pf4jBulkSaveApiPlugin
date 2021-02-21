package com.opsmx.plugin.gate.controllers

import com.netflix.spinnaker.gate.api.extension.ApiExtension
import com.netflix.spinnaker.gate.api.extension.HttpRequest
import com.netflix.spinnaker.gate.api.extension.HttpResponse
import com.netflix.spinnaker.kork.exceptions.HasAdditionalAttributes
import com.netflix.spinnaker.kork.plugins.api.PluginConfiguration
import com.netflix.spinnaker.security.AuthenticatedRequest
import com.opsmx.spinnaker.gate.services.BatchUpdateTaskService
import groovy.transform.CompileDynamic
import groovy.transform.InheritConstructors
import io.swagger.annotations.ApiOperation
import org.jetbrains.annotations.NotNull
import org.pf4j.Extension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

import javax.annotation.Nonnull

@Extension
public class BatchUpdateApiController implements ApiExtension {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    BatchUpdateTaskService bulkSaveTaskService

    BatchUpdateApiController(BatchUpdateTaskService bulkSaveTaskService) {
        this.bulkSaveTaskService = bulkSaveTaskService;
    }

    @CompileDynamic
    @ApiOperation(value = "Save an array of pipeline definition")
    @PostMapping('/batchUpdateabcd')
    Map batchUpdateApiPipeline(@RequestBody List<Map> pipelineList) {

        def job = [
                type       : "savePipeline",
                pipeline   : pipelineList,
                user       : AuthenticatedRequest.spinnakerUser.orElse("anonymous"),
                name       : "bulk save pipeline",
                application: "bulk save application"
        ]
        def result = bulkSaveTaskService.bulkCreateAndWaitForCompletion(job)
        return result
    }

    @Override
    String id() {
        return  ApiExtensionConfigProperties.getId();
    }

    @Override
    boolean handles(@NotNull @Nonnull HttpRequest httpRequest) {
        return supportedGet(httpRequest) || supportedPost(httpRequest)
    }

    @Override
    HttpResponse handle(@NotNull @Nonnull HttpRequest httpRequest) {
        log.info( " id : " + id())
        if (supportedPost(httpRequest)) {
            return get()
        }
        return echo(httpRequest)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @InheritConstructors
    class PipelineException extends RuntimeException implements HasAdditionalAttributes {
        Map<String, Object> additionalAttributes = [:]

        PipelineException(String message) {
            super(message)
        }

        PipelineException(Map<String, Object> additionalAttributes) {
            this.additionalAttributes = additionalAttributes
        }
    }

    @PluginConfiguration("api-extension")
    static class ApiExtensionConfigProperties {

        private String id;

        String getId() {
            return id
        }

        void setId(String id) {
            this.id = id
        }
    }

    private Boolean supportedGet(@Nonnull HttpRequest httpRequest){
        return httpRequest.method.equalsIgnoreCase("GET") &&
                httpRequest.requestURI.endsWith("")
    }

    private Boolean supportedPost(@Nonnull HttpRequest httpRequest) {
        return httpRequest.method.equalsIgnoreCase("PUT") &&
                httpRequest.requestURI.endsWith("/batchUpdate")
    }

    private HttpResponse get() {
        return  HttpResponse.of(204, emptyMap(), null)
    }

    private HttpResponse echo(@Nonnull HttpRequest httpRequest) {
        Map<String, String> echo = httpRequest.parameters["parameter"]
        return HttpResponse.of(200, emptyMap(), echo)
    }
}

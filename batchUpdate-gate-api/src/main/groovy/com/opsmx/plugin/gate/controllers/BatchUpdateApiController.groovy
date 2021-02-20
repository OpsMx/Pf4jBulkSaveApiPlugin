package com.opsmx.plugin.gate.controllers

import com.netflix.spinnaker.kork.exceptions.HasAdditionalAttributes
import com.netflix.spinnaker.kork.web.exceptions.NotFoundException
import com.netflix.spinnaker.security.AuthenticatedRequest
import com.opsmx.plugin.gate.services.BatchUpdateTaskService
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import io.swagger.annotations.ApiOperation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import retrofit.RetrofitError

@CompileStatic
@RestController
@RequestMapping("/pipelines")
public class BatchUpdateApiController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    BatchUpdateTaskService bulkSaveTaskService

    public BatchUpdateApiController(BatchUpdateTaskService bulkSaveTaskService) {
        this.bulkSaveTaskService = bulkSaveTaskService;
    }

    @CompileDynamic
    @ApiOperation(value = "Save an array of pipeline definition")
    @PostMapping('/batchUpdate')
    Map batchUpdateApiPipeline(@RequestBody List<Map> pipelineList) {

        def job = [
                    type      : "savePipeline",
                    pipeline  : pipelineList,
                    user      : AuthenticatedRequest.spinnakerUser.orElse("anonymous"),
                    name      : "bulk save pipeline",
                    application: "bulk save application"
                  ]
        def result = bulkSaveTaskService.bulkCreateAndWaitForCompletion(job)
        return result
    }

    @ApiOperation(value = "Retrieve a pipeline execution")
    @GetMapping("/testCall")
    Map getApiPipeline() {
        log.info(" test call")
        Map<String,String> m = new HashMap<>();
        m.put("abcd", "efgh");
        log.info(" after map ")
        try {
            log.info(" in try block ")
        } catch (RetrofitError e) {
            if (e.response?.status == 404) {
                throw new NotFoundException("Pipeline not found (id:")
            }
        }
        return m;
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
}

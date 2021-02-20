package com.opsmx.plugin.batchUpdate.api;

import com.netflix.spinnaker.kork.plugins.api.spring.SpringLoaderPlugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@ComponentScan(basePackages = "com.opsmx.plugin.bulksave.api")
@Configuration
public class BatchUpdateApiPlugin extends SpringLoaderPlugin {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor to be used by plugin manager for plugin instantiation. Your plugins have to provide
     * constructor with this exact signature to be successfully loaded by manager.
     *
     * @param wrapper
     */
    public BatchUpdateApiPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public List<String> getPackagesToScan() {
        return Collections.singletonList("com.opsmx.plugin.gate");
    }

    public void start() {
        log.info("BatchUpdateApiPlugin.start()");
    }

    public void stop() {
        log.info("BatchUpdateApiPlugin.stop()");
    }
}

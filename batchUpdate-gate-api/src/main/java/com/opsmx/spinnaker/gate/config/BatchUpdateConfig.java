package com.opsmx.spinnaker.gate.config;

import com.opsmx.spinnaker.gate.services.internal.Front50Api;
import com.opsmx.spinnaker.gate.services.internal.RestOk3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchUpdateConfig {

    @Bean
    Front50Api front50Api() {
        return new RestOk3Client().getClient();
    }
}

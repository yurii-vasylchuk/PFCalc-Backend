package org.mvasylchuk.pfcc.platform.configuration;

import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties({PfccAppConfigurationProperties.class})
@EnableScheduling
@EnableBatchProcessing
public class AppConfiguration {
}

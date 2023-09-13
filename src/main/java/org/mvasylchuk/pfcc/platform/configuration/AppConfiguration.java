package org.mvasylchuk.pfcc.platform.configuration;

import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PfccAppConfigurationProperties.class})
public class AppConfiguration {
}

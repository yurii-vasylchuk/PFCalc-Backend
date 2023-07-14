package org.mvasylchuk.pfcc.platform.configuration;

import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccSecurityConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PfccSecurityConfigurationProperties.class, PfccAppConfigurationProperties.class})
public class AppConfiguration {

}

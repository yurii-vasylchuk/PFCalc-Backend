package org.mvasylchuk.pfcc.platform.configuration.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pfcc.app", ignoreUnknownFields = false)
@RequiredArgsConstructor
@Getter
public class PfccAppConfigurationProperties {
    public final MailConfiguration mail;

    @RequiredArgsConstructor
    @Getter
    public static class MailConfiguration {
        public final Boolean enabled;
    }
}

package org.mvasylchuk.pfcc.platform.configuration.model;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "pfcc.security")
@RequiredArgsConstructor
public class PfccSecurityConfigurationProperties {
    public final PfccJwtConfiguration jwt;

    @RequiredArgsConstructor
    public static class PfccJwtConfiguration {
        public final String publicKey;
        public final String privateKey;
        public final String keyAlgorithm;
        public final String issuer;
        public final Duration expiration;

    }
}

package org.mvasylchuk.pfcc.platform.configuration.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "pfcc", ignoreUnknownFields = false)
@RequiredArgsConstructor
public class PfccAppConfigurationProperties {
    public final MailConfiguration mail;
    public final PfccAppConfigurationProperties.PfccJwtConfiguration jwt;
    public final PfccAppConfigurationProperties.AwsConfiguration aws;
    public final List<String> cors;
    public final Boolean exposeException;

    @RequiredArgsConstructor
    public static class MailConfiguration {
        public final Boolean enabled;
        public final String doNotReplyAddress;
    }

    @RequiredArgsConstructor
    public static class PfccJwtConfiguration {
        public final String publicKey;
        public final String privateKey;
        public final String keyAlgorithm;
        public final String issuer;
        public final Duration expiration;

    }

    @RequiredArgsConstructor
    public static class AwsConfiguration {
        @NonNull
        private final String region;
        @NonNull
        public final AwsCredentialsType credentialsType;
        public final String profile;
        public Region getRegion() {
            return Region.of(this.region);
        }

        @RequiredArgsConstructor
        public enum AwsCredentialsType {
            PROFILE, INSTANCE_PROFILE;
            public static final String PROFILE_VALUE = "PROFILE";
            public static final String INSTANCE_PROFILE_VALUE = "INSTANCE_PROFILE";
        }
    }
}

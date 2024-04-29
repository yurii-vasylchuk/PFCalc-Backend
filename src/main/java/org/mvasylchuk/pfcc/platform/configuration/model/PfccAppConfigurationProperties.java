package org.mvasylchuk.pfcc.platform.configuration.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.Cookie;
import software.amazon.awssdk.regions.Region;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "pfcc", ignoreUnknownFields = false)
@RequiredArgsConstructor
public class PfccAppConfigurationProperties {
    public final MailConfiguration mail;
    public final PfccAuthConfiguration auth;
    public final PfccAppConfigurationProperties.AwsConfiguration aws;
    @NonNull
    public final JobConfiguration jobs;
    public final List<String> cors;
    public final Boolean exposeException;
    @NonNull
    public final ReportingConfiguration reports;

    @RequiredArgsConstructor
    public static class MailConfiguration {
        public final Boolean enabled;
        public final String doNotReplyAddress;
    }

    @RequiredArgsConstructor
    public static class PfccAuthConfiguration {
        public final String publicKey;
        public final String privateKey;
        public final String keyAlgorithm;
        public final String issuer;
        public final Duration authTokenExpiration;
        public final Duration refreshTokenExpiration;
        public final Cookie.SameSite sameSite;
    }

    @RequiredArgsConstructor
    public static class AwsConfiguration {
        @NonNull
        public final AwsCredentialsType credentialsType;
        public final String profile;
        @NonNull
        private final String region;

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

    @RequiredArgsConstructor
    public static class JobConfiguration {
        @NonNull
        public final DropOutdatedSecurityTokensConfiguration dropOutdatedSecurityTokens;

        @RequiredArgsConstructor
        public static class DropOutdatedSecurityTokensConfiguration {
            public final Boolean enabled;
            public final String cron;
            public final Duration outdatedSecurityTokenTtl;
        }
    }

    @RequiredArgsConstructor
    public static class ReportingConfiguration {
        @NonNull
        public final Path storePath;
        @NonNull
        public final String chromeExecutable;
        @NonNull
        public final Duration renderTimeout;
    }
}

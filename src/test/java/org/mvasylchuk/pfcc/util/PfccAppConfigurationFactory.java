package org.mvasylchuk.pfcc.util;

import lombok.NonNull;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.springframework.boot.web.server.Cookie;

import java.time.Duration;
import java.util.List;

public class PfccAppConfigurationFactory {
    public static PfccAppConfigurationProperties pfccConf(
            PfccAppConfigurationProperties.MailConfiguration mailConf, PfccAppConfigurationProperties.PfccAuthConfiguration jwtConf, PfccAppConfigurationProperties.AwsConfiguration awsConf, PfccAppConfigurationProperties.@NonNull JobConfiguration jobsConf, List<String> corsConf, Boolean exposeExceptions) {
        return new PfccAppConfigurationProperties(
                mailConf,
                jwtConf,
                awsConf,
                jobsConf,
                corsConf,
                exposeExceptions
        );
    }

    public static PfccAppConfigurationProperties.MailConfiguration mail(Boolean enabled, String doNotReplyAddress) {
        return new PfccAppConfigurationProperties.MailConfiguration(
                enabled,
                doNotReplyAddress
        );
    }

    public static PfccAppConfigurationProperties.PfccAuthConfiguration auth(String publicKey, String privateKey, String algorythm, String iss, String authExp, String refreshExp, Cookie.SameSite sameSite) {
        return new PfccAppConfigurationProperties.PfccAuthConfiguration(
                publicKey,
                privateKey,
                algorythm,
                iss,
                Duration.parse(authExp),
                Duration.parse(refreshExp),
                sameSite
        );
    }

    public static PfccAppConfigurationProperties.AwsConfiguration aws(@NonNull String region, PfccAppConfigurationProperties.AwsConfiguration.@NonNull AwsCredentialsType type, String profile) {
        return new PfccAppConfigurationProperties.AwsConfiguration(
                type,
                profile,
                region
        );
    }

    public static PfccAppConfigurationProperties.JobConfiguration jobs(PfccAppConfigurationProperties.JobConfiguration.@NonNull DropOutdatedSecurityTokensConfiguration dropOutdatedSecTokensConf) {
        return new PfccAppConfigurationProperties.JobConfiguration(
                dropOutdatedSecTokensConf
        );
    }

    public static PfccAppConfigurationProperties.JobConfiguration.DropOutdatedSecurityTokensConfiguration dropOutdatedSecTokensConf(Boolean enabled, String cron, String ttl) {
        return new PfccAppConfigurationProperties.JobConfiguration.DropOutdatedSecurityTokensConfiguration(
                enabled,
                cron,
                ttl == null ? null : Duration.parse(ttl)
        );
    }
}

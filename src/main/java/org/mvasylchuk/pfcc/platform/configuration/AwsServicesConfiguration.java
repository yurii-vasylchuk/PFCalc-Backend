package org.mvasylchuk.pfcc.platform.configuration;

import org.mvasylchuk.pfcc.platform.configuration.annotations.ConditionalOnMailEnabled;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sesv2.SesV2Client;

import static org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties.AwsConfiguration.AwsCredentialsType.INSTANCE_PROFILE_VALUE;
import static org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties.AwsConfiguration.AwsCredentialsType.PROFILE_VALUE;

@Configuration
public class AwsServicesConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "pfcc.aws",
                           value = {"credentials-type", "credentialsType"},
                           havingValue = INSTANCE_PROFILE_VALUE)
    public AwsCredentialsProvider iamCredentialsProvider() {
        return ProfileCredentialsProvider.create();
    }

    @Bean
    @ConditionalOnProperty(prefix = "pfcc.aws",
                           value = {"credentials-type", "credentialsType"},
                           havingValue = PROFILE_VALUE)
    public AwsCredentialsProvider authTokenCredentialsProvider(PfccAppConfigurationProperties conf) {
        return ProfileCredentialsProvider.create(conf.aws.profile);
    }

    @Bean
    @ConditionalOnMailEnabled
    @ConditionalOnBean(AwsCredentialsProvider.class)
    public SesV2Client sesV2Client(PfccAppConfigurationProperties conf, AwsCredentialsProvider credentialsProvider) {
        return SesV2Client.builder().region(conf.aws.getRegion()).credentialsProvider(credentialsProvider).build();
    }

}

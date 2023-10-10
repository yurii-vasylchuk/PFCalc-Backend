package org.mvasylchuk.pfcc.platform.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.platform.configuration.annotations.ConditionalOnMailEnabled;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.user.Language;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

import java.util.Map;

@Component
@Slf4j
@ConditionalOnMailEnabled
@RequiredArgsConstructor
public class SesEmailService implements EmailService {
    private final SesV2Client client;
    private final ObjectMapper objectMapper;
    private final PfccAppConfigurationProperties conf;

    @Override
    public void sendEmailVerificationMail(String address, String name, String token, Language preferredLanguage) {
        String emailTemplateName = switch (preferredLanguage) {
            case UA -> "VerifyEmailUA";
            case EN -> "VerifyEmailEN";
        };

        try {
            final String templateData = objectMapper.writeValueAsString(
                    Map.of("name", name,
                            "token", token)
            );

            client.sendEmail(emailBuilder -> emailBuilder
                    .destination(destBuilder -> destBuilder
                            .toAddresses(address))
                    .content(contentBuilder -> contentBuilder
                            .template(templateBuilder -> templateBuilder
                                    .templateName(emailTemplateName)
                                    .templateData(templateData)))
                    .fromEmailAddress(conf.mail.doNotReplyAddress)
            );
        } catch (JsonProcessingException e) {
            log.error("Can't prepare data for 'email verification' template", e);
            throw new RuntimeException(e);
        } catch (SesV2Exception e) {
            log.error("Failed to send 'email verification' email", e);
        }
    }

    @Override
    public void sendEmailVerifiedConfirmation(String email, String name, Language preferredLanguage) {
        String emailTemplateName = switch (preferredLanguage) {
            case UA -> "EmailVerifiedUA";
            case EN -> "EmailVerifiedEN";
        };

        try {
            final String templateData = objectMapper.writeValueAsString(
                    Map.of("name", name)
            );

            client.sendEmail(emailBuilder -> emailBuilder
                    .destination(destBuilder -> destBuilder
                            .toAddresses(email))
                    .content(contentBuilder -> contentBuilder
                            .template(templateBuilder -> templateBuilder
                                    .templateName(emailTemplateName)
                                    .templateData(templateData)))
                    .fromEmailAddress(conf.mail.doNotReplyAddress)
            );
        } catch (JsonProcessingException e) {
            log.error("Can't prepare data for 'email verified' template", e);
            throw new RuntimeException(e);
        } catch (SesV2Exception e) {
            log.error("Failed to send 'email verified' email", e);
        }
    }
}

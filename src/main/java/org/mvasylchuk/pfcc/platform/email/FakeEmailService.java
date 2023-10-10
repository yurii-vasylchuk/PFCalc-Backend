package org.mvasylchuk.pfcc.platform.email;

import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.platform.configuration.annotations.ConditionalOnMailDisabled;
import org.mvasylchuk.pfcc.user.Language;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@ConditionalOnMailDisabled
public class FakeEmailService implements EmailService {

    @Override
    public void sendEmailVerificationMail(String address, String name, String token, Language preferredLanguage) {
        log.info("""
                sending email verification to {}<{}>
                token: {}
                preferred language: {}
                """, address, name, token, preferredLanguage.name());
    }

    @Override
    public void sendEmailVerifiedConfirmation(String email, String name, Language preferredLanguage) {
        log.info("""
                sending email verification confirmation to {}<{}>
                preferred language: {}
                """, email, name, preferredLanguage);
    }
}

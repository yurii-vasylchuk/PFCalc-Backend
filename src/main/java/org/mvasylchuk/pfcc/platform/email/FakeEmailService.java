package org.mvasylchuk.pfcc.platform.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@ConditionalOnProperty(name = "pfcc.mail.enabled", havingValue = "false", matchIfMissing = true)
public class FakeEmailService implements EmailService {
    @Override
    public void sendEmail(String recipient, String body) {
        log.info("""
                sending email to {}
                body:
                {}
                """, recipient, body);
    }
}

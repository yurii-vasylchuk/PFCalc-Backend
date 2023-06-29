package org.mvasylchuk.pfcc.platform.email;

public interface EmailService {
    void sendEmail(String recipient, String body);
}

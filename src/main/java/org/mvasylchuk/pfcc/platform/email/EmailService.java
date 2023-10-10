package org.mvasylchuk.pfcc.platform.email;

import org.mvasylchuk.pfcc.user.Language;

public interface EmailService {
    void sendEmailVerificationMail(String address, String name, String token, Language preferredLanguage);

    void sendEmailVerifiedConfirmation(String email, String name, Language preferredLanguage);
}

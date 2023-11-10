package org.mvasylchuk.pfcc.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mvasylchuk.pfcc.platform.email.EmailService;
import org.mvasylchuk.pfcc.platform.jwt.JwtService;
import org.mvasylchuk.pfcc.securitytoken.SecurityTokenService;
import org.mvasylchuk.pfcc.user.dto.AuthTokensDto;
import org.mvasylchuk.pfcc.user.dto.RegisterRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mvasylchuk.pfcc.securitytoken.SecurityTokenType.EMAIL_VERIFICATION;
import static org.mvasylchuk.pfcc.securitytoken.SecurityTokenType.REFRESH_TOKEN;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityTokenService securityTokenService;

    @Test
    void register() {
        String validationTokenCode = "VALIDATION_TOKEN_CODE";
        String refreshTokenCode = "REFRESH_TOKEN_CODE";
        String accessToken = "ACCESS_TOKEN";

        Mockito.when(passwordEncoder.encode(any())).thenReturn("pass");
        Mockito.when(userRepository.save(any())).thenReturn(null);
        Mockito.doNothing()
               .when(emailService)
               .sendEmailVerificationMail(eq("email"), eq("name"), eq(validationTokenCode), eq(Language.UA));
        Mockito.when(jwtService.generateToken(any())).thenReturn(accessToken);
        Mockito.when(securityTokenService.generateSecurityToken(any(), eq(EMAIL_VERIFICATION)))
               .thenReturn(validationTokenCode);
        Mockito.when(securityTokenService.generateSecurityToken(any(), eq(REFRESH_TOKEN)))
               .thenReturn(refreshTokenCode);

        AuthTokensDto result = underTest.register(new RegisterRequestDto("email", "password", "name", Language.UA));

        Assertions.assertEquals(accessToken, result.getAccessToken());
        Assertions.assertEquals(refreshTokenCode, result.getRefreshToken());

    }

}

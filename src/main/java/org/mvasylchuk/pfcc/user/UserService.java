package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.platform.email.EmailService;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.platform.jwt.JwtService;
import org.mvasylchuk.pfcc.securitytoken.SecurityTokenService;
import org.mvasylchuk.pfcc.securitytoken.SecurityTokenType;
import org.mvasylchuk.pfcc.user.dto.AuthTokensDto;
import org.mvasylchuk.pfcc.user.dto.LoginRequestDto;
import org.mvasylchuk.pfcc.user.dto.ProfileDto;
import org.mvasylchuk.pfcc.user.dto.RefreshAuthTokenRequestDto;
import org.mvasylchuk.pfcc.user.dto.RegisterRequestDto;
import org.mvasylchuk.pfcc.user.dto.SaveProfileRequestDto;
import org.mvasylchuk.pfcc.user.dto.VerifyAccountRequestDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserJooqRepository userJooqRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SecurityTokenService securityTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final PfccAppConfigurationProperties conf;

    public AuthTokensDto register(RegisterRequestDto request) {
        UserEntity user = new UserEntity(null,
                                         request.getEmail(),
                                         passwordEncoder.encode(request.getPassword()),
                                         request.getName(),
                                         request.getPreferredLanguage(),
                                         null,
                                         false,
                                         List.of(UserRole.USER));

        userRepository.save(user);

        String emailVerificationToken = securityTokenService.generateSecurityToken(user,
                                                                                   SecurityTokenType.EMAIL_VERIFICATION);
        emailService.sendEmailVerificationMail(request.getEmail(),
                                               request.getName(),
                                               emailVerificationToken,
                                               request.getPreferredLanguage());

        return generateAuthTokens(user);
    }

    public void saveProfile(SaveProfileRequestDto request) {
        UserEntity user = currentUser();
        if (request.getAims() != null) {
            Pfcc aims = new Pfcc(request.getAims().getProtein(),
                                 request.getAims().getFat(),
                                 request.getAims().getCarbohydrates(),
                                 request.getAims().getCalories());
            user.setAims(aims);
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPreferredLanguage() != null) {
            if (Arrays.stream(Language.values())
                    .noneMatch(lang -> lang.name().equals(request.getPreferredLanguage()))) {
                throw new PfccException("Provided language is unsupported", ApiErrorCode.LANGUAGE_IS_UNSUPPORTED);
            }

            user.setPreferredLanguage(Language.valueOf(request.getPreferredLanguage()));
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
    }

    public AuthTokensDto verifyAccount(VerifyAccountRequestDto verificationRequest) {
        UserEntity user = securityTokenService.validate(verificationRequest.token(),
                                                        SecurityTokenType.EMAIL_VERIFICATION);
        user.setEmailConfirmed(true);
        userRepository.save(user);

        emailService.sendEmailVerifiedConfirmation(user.getEmail(), user.getName(), user.getPreferredLanguage());

        return generateAuthTokens(user);
    }

    public UserEntity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        } else {
            return userRepository.getByEmail(auth.getName());
        }
    }

    public AuthTokensDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                                        .orElseThrow(() -> new PfccException(ApiErrorCode.SECURITY));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PfccException("Password doesn't match", ApiErrorCode.SECURITY);
        }

        return generateAuthTokens(user);
    }

    public ProfileDto getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        } else {
            return userJooqRepository.getProfileByUserEmail(auth.getName());

        }
    }

    public AuthTokensDto refreshAuth(RefreshAuthTokenRequestDto request) {
        UserEntity user = securityTokenService.validate(request.getRefreshToken(), SecurityTokenType.REFRESH_TOKEN);
        return generateAuthTokens(user);
    }

    private AuthTokensDto generateAuthTokens(UserEntity user) {
        String refreshToken = securityTokenService.generateSecurityToken(user,
                                                                         SecurityTokenType.REFRESH_TOKEN,
                                                                         LocalDateTime.now()
                                                                                      .plus(conf.auth.refreshTokenExpiration));
        String token = jwtService.generateToken(user);
        return new AuthTokensDto(token, refreshToken);
    }
}

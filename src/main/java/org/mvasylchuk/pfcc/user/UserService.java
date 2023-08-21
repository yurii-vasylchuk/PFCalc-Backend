package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.platform.email.EmailService;
import org.mvasylchuk.pfcc.platform.jwt.JwtService;
import org.mvasylchuk.pfcc.user.dto.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserJooqRepository userJooqRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AccessTokenDto register(RegisterRequestDto request) {
        UserEntity user = new UserEntity(null, request.getEmail(), passwordEncoder.encode(request.getPassword()), Language.UA, null, false, false, Collections.emptyList());
        userRepository.save(user);
        sendEmail(request);
        String token = jwtService.generateToken(user);
        return new AccessTokenDto(token);
    }

    public void completeProfile(CompleteProfileRequestDto request) {
        UserEntity user = currentUser();
        Pfcc aims = new Pfcc(request.getAims().getProtein(),
                request.getAims().getFat(),
                request.getAims().getCarbohydrates(),
                request.getAims().getCalories());
        user.setAims(aims);
        user.setProfileConfigured(true);
        userRepository.save(user);
    }

    private void sendEmail(RegisterRequestDto request) {
        emailService.sendEmail(request.getEmail(), """
                Вітаємо Вас на платформі, підтвердіть свій e-mail
                """);
    }

    public UserEntity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        } else {
            return userRepository.getByEmail(auth.getName());

        }
    }

    public AccessTokenDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password doesn't match");
        }

        String token = jwtService.generateToken(user);

        return new AccessTokenDto(token);
    }

    public ProfileDto getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        } else {
            return userJooqRepository.getProfileByUserEmail(auth.getName());

        }
    }
}

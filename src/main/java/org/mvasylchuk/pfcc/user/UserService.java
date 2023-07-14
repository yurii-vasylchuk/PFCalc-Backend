package org.mvasylchuk.pfcc.user;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.platform.email.EmailService;
import org.mvasylchuk.pfcc.platform.jwt.JwtService;
import org.mvasylchuk.pfcc.user.dto.AccessTokenDto;
import org.mvasylchuk.pfcc.user.dto.LoginRequestDto;
import org.mvasylchuk.pfcc.user.dto.RegisterRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
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

    private void sendEmail(RegisterRequestDto request) {
        emailService.sendEmail(request.getEmail(), """
                Вітаємо Вас на платформі, підтвердіть свій e-mail
                """);
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
}

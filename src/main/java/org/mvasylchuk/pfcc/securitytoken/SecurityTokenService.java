package org.mvasylchuk.pfcc.securitytoken;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.user.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SecurityTokenService {
    private static final char[] POSSIBLE_CODE_SYMBOLS = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'l', 'k', 'j', 'h', 'g', 'f', 'd', 's', 'a', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'B', 'V', 'C', 'X', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '+'};
    private static final int CODE_LENGTH = 255;

    private final SecurityTokenJpaRepository repository;
    private final Random rnd = new Random();

    public String generateSecurityToken(UserEntity user, SecurityTokenType type) {
        return this.generateSecurityToken(user, type, null);
    }

    public String generateSecurityToken(UserEntity user, SecurityTokenType type, LocalDateTime validUntil) {
        SecurityTokenEntity securityToken = new SecurityTokenEntity();

        securityToken.setUser(user);
        securityToken.setType(type);
        securityToken.setIsActive(true);
        securityToken.setCode(generateCode());
        securityToken.setValidUntil(validUntil);

        repository.save(securityToken);
        return securityToken.getCode();
    }

    public UserEntity validate(String code, SecurityTokenType type) {
        SecurityTokenEntity token = repository.findValid(code, type)
                                              .orElseThrow(() -> new IllegalArgumentException("Invalid security token code: token doesn't exists"));

        token.setIsActive(false);
        repository.save(token);

        return token.getUser();
    }

    private String generateCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            builder.append(POSSIBLE_CODE_SYMBOLS[rnd.nextInt(POSSIBLE_CODE_SYMBOLS.length)]);
        }
        return builder.toString();
    }
}

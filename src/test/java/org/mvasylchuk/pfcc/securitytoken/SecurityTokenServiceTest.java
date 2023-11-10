package org.mvasylchuk.pfcc.securitytoken;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mvasylchuk.pfcc.user.Language;
import org.mvasylchuk.pfcc.user.UserEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;
import static org.mvasylchuk.pfcc.securitytoken.SecurityTokenType.EMAIL_VERIFICATION;

@ExtendWith(MockitoExtension.class)
class SecurityTokenServiceTest {
    @Mock
    private SecurityTokenJpaRepository repository;
    @InjectMocks
    private SecurityTokenService underTest;
    @Captor
    ArgumentCaptor<SecurityTokenEntity> tokenCaptor;


    @Test
    void generateSecurityToken() {
        when(repository.save(tokenCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        UserEntity userEntity = new UserEntity(1L, "email@email.com", "password", "name", Language.UA, null, true, true, Collections.emptyList());
        String code = underTest.generateSecurityToken(userEntity, EMAIL_VERIFICATION);

        List<SecurityTokenEntity> captured = tokenCaptor.getAllValues();

        assertNotNull(code);

        assertThat(captured)
                .hasSize(1)
                .allMatch(token -> token.getType() == EMAIL_VERIFICATION &&
                        token.getCode().equals(code) &&
                        token.getIsActive() &&
                        token.getUser() == userEntity);
    }

    @Test
    void validate() {
        UserEntity userEntity = new UserEntity(1L, "email@email.com", "password", "name", Language.UA, null, true, true, Collections.emptyList());
        String code = "TEST_CODE";

        when(repository.findValid(code, EMAIL_VERIFICATION))
                .thenReturn(Optional.of(new SecurityTokenEntity(1L, code, userEntity, EMAIL_VERIFICATION, true, null)));

        when(repository.save(tokenCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity actualUser = underTest.validate(code, EMAIL_VERIFICATION);

        assertSame(userEntity, actualUser);

        assertThat(tokenCaptor.getAllValues())
                .hasSize(1)
                .allMatch(token -> token.getType() == EMAIL_VERIFICATION &&
                        token.getCode().equals(code) &&
                        !token.getIsActive() &&
                        token.getUser() == userEntity);

    }
}

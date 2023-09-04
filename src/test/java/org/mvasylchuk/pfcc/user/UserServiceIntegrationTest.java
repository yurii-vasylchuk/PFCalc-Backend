package org.mvasylchuk.pfcc.user;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.user.dto.CompleteProfileRequestDto;
import org.mvasylchuk.pfcc.user.dto.RegisterRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService underTest;
    @Autowired
    private UserRepository userRepository;

    @Test
    void register() {

        underTest.register(new RegisterRequestDto("email", "password", "name", Language.UA));
        List<UserEntity> users = userRepository.findAll();
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getEmail(), "email");
        assertEquals(users.get(0).getName(), "name");
        assertEquals(users.get(0).getPreferredLanguage(), Language.UA);

    }

    @Test
    @FlywayTest(locationsForMigrate = "migration/UserService/CompleteProfile")
    void completeProfile() {
        SecurityContextHolder.getContext()
                             .setAuthentication(UsernamePasswordAuthenticationToken.authenticated("email", "pass", List.of()));

        underTest.completeProfile(new CompleteProfileRequestDto(new PfccDto(new BigDecimal(10),
                new BigDecimal(50), new BigDecimal(30), new BigDecimal(550))));
        UserEntity user = userRepository.getByEmail("email");
        org.assertj.core.api.Assertions.assertThat(user.getAims().getProtein()).isEqualByComparingTo("10");
        org.assertj.core.api.Assertions.assertThat(user.getAims().getCarbohydrates()).isEqualByComparingTo("30");
        org.assertj.core.api.Assertions.assertThat(user.getAims().getFat()).isEqualByComparingTo("50");
        org.assertj.core.api.Assertions.assertThat(user.getAims().getCalories()).isEqualByComparingTo("550");
    }
}

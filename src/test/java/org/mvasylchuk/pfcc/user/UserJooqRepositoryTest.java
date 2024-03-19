package org.mvasylchuk.pfcc.user;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.user.dto.ProfileDto;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
class UserJooqRepositoryTest {
    @Autowired
    UserJooqRepository repository;

    @Test
    @FlywayTest(locationsForMigrate = "migration/UserJooqRepositoryTest")
    void getProfile() {
        ProfileDto result = repository.getProfileByUserEmail("yva@test.com");

        assertEquals("name", result.getName());
        assertEquals("yva@test.com", result.getEmail());
        assertEquals(Language.UA, result.getPreferredLanguage());
        assertNotNull(result.getAims());
        assertThat(result.getAims().getProtein())
                .isEqualByComparingTo("120");
        assertThat(result.getAims().getFat())
                .isEqualByComparingTo("50");
        assertThat(result.getAims().getCarbohydrates())
                .isEqualByComparingTo("200");
        assertThat(result.getAims().getCalories())
                .isEqualByComparingTo("1000");
    }
}

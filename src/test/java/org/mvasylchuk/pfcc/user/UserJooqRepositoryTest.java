package org.mvasylchuk.pfcc.user;

import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.user.dto.ProfileDto;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class UserJooqRepositoryTest {
    @Autowired
    UserJooqRepository repository;

    @Test
    @FlywayTest(locationsForMigrate = "migration/UserJooqRepositoryTest")
    void getProfile() {
        ProfileDto result = repository.getProfileByUserEmail("yva@test.com");
        Assertions.assertThat(result.getDishes())
                .hasSize(2)
                .anyMatch(dishDto -> dishDto.getId().equals(1L))
                .anyMatch(dishDto -> dishDto.getId().equals(3L));
        Assertions.assertThat(result.getMeals())
                .hasSize(2)
                .anyMatch(mealDto -> mealDto.getId().equals(1L))
                .anyMatch(mealDto -> mealDto.getId().equals(5L));
    }
}

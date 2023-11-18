package org.mvasylchuk.pfcc.domain.service;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.util.WithTestUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@IntegrationTest
class FoodServiceTest {
    @Autowired
    FoodService service;
    @Autowired
    FoodRepository repository;

    @Test
    void addFood() {
    }

    @Test
    @FlywayTest(locationsForMigrate = "migration/FoodServiceTest/removeFood")
    @WithTestUser(id = 1, email = "yva@test.com", roles = "USER")
    void remove() {
        service.remove(1L);
        Optional<FoodEntity> food = repository.findById(1L);

        assertThat(food).isPresent()
                .get()
                .extracting(FoodEntity::getIsDeleted)
                .isEqualTo(true);
    }
}
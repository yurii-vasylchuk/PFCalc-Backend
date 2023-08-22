package org.mvasylchuk.pfcc.domain.service;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;


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
    void remove() {

        service.remove(1L);
        Assertions.assertTrue(repository.findById(1L).get().getIsDeleted());

    }
}
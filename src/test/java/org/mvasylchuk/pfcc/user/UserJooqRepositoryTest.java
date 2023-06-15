package org.mvasylchuk.pfcc.user;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class UserJooqRepositoryTest {
    @Autowired
    UserJooqRepository repository;

    @Test
    @FlywayTest(locationsForMigrate = "migration/UserJooqRepositoryTest")
    void getEmailById() {
        String email = repository.getEmailById(1L);
        assertEquals("yva@test.com", email);
    }
}

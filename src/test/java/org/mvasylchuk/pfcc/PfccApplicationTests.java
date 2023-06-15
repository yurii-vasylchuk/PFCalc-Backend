package org.mvasylchuk.pfcc;

import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit5.FlywayTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith({DatabaseSetupExtension.class, FlywayTestExtension.class})
class PfccApplicationTests {

    @Test
    @FlywayTest(locationsForMigrate = "migration/test")
    void contextLoads() {
    }

}

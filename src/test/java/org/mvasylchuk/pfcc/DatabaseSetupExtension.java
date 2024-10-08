package org.mvasylchuk.pfcc;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
public class DatabaseSetupExtension implements BeforeEachCallback, BeforeAllCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        var flyway = SpringExtension.getApplicationContext(context).getBean(Flyway.class);
        flyway.clean();
        flyway.migrate();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        PfccPostgresContainer.container.start();
        updateDataSourceProps(PfccPostgresContainer.container);
    }

    private void updateDataSourceProps(PfccPostgresContainer container) {
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());

        log.info("DB testcontainers connection: {}:{} {}",
                container.getUsername(),
                container.getPassword(),
                container.getJdbcUrl());
    }
}

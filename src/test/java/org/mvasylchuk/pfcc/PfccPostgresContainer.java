package org.mvasylchuk.pfcc;

import org.testcontainers.containers.PostgreSQLContainer;

public class PfccPostgresContainer extends PostgreSQLContainer<PfccPostgresContainer> {
    public static final String IMAGE_VERSION = "postgres:16.0-alpine";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final String DATABASE_NAME = "pfcc";

    public static final PfccPostgresContainer container = new PfccPostgresContainer()
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withReuse(true);

    private PfccPostgresContainer() {
        super(IMAGE_VERSION);
    }
}

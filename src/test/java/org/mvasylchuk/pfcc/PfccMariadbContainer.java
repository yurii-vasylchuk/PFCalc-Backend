package org.mvasylchuk.pfcc;

import org.testcontainers.containers.MariaDBContainer;

public class PfccMariadbContainer extends MariaDBContainer<PfccMariadbContainer> {
    public static final String IMAGE_VERSION = "mariadb:10.7.8";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";
    public static final String DATABASE_NAME = "pfcc";

    public static final PfccMariadbContainer container = new PfccMariadbContainer()
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withReuse(true);

    private PfccMariadbContainer() {
        super(IMAGE_VERSION);
    }
}

package org.mvasylchuk.pfcc.api.glue;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MariaDBContainer;

import javax.sql.DataSource;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("apitest")
public class SpringBootTestLoader {
    public static final String IMAGE_VERSION = "mariadb:10.7.8";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";
    public static final String DATABASE_NAME = "pfcc";

    static MariaDBContainer<?> mariaDBContainer;

    @BeforeAll
    public static void setup() {
        mariaDBContainer = new MariaDBContainer<>(IMAGE_VERSION)
                .withDatabaseName(DATABASE_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);
        mariaDBContainer.start();
        System.out.println(mariaDBContainer.getJdbcUrl());
    }

    @TestConfiguration
    static class PostgresTestConfiguration {
        @Bean
        DataSource dataSource() {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(mariaDBContainer.getJdbcUrl());
            hikariConfig.setUsername(mariaDBContainer.getUsername());
            hikariConfig.setPassword(mariaDBContainer.getPassword());
            return new HikariDataSource(hikariConfig);
        }
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("closing DB connection");
        mariaDBContainer.stop();
    }
}

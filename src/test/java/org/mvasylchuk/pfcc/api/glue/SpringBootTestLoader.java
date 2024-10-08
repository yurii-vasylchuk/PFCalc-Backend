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
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("apitest")
public class SpringBootTestLoader {
    public static final String IMAGE_VERSION = "postgres:16.0-alpine";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final String DATABASE_NAME = "pfcc";

    static PostgreSQLContainer<?> postgreSQLContainer;

    @BeforeAll
    public static void setup() {
        postgreSQLContainer = new PostgreSQLContainer<>(IMAGE_VERSION)
                .withDatabaseName(DATABASE_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);
        postgreSQLContainer.start();
        System.out.println(postgreSQLContainer.getJdbcUrl());
    }

    @TestConfiguration
    static class PostgresTestConfiguration {
        @Bean
        DataSource dataSource() {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
            hikariConfig.setUsername(postgreSQLContainer.getUsername());
            hikariConfig.setPassword(postgreSQLContainer.getPassword());
            return new HikariDataSource(hikariConfig);
        }

        @Bean
        @Primary
        public PasswordEncoder passwordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("closing DB connection");
        postgreSQLContainer.stop();
    }
}

package org.mvasylchuk.pfcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MariaDBContainer;

@TestConfiguration(proxyBeanMethods = false)
class TestPfccApplication {

	@Bean
	@ServiceConnection
	MariaDBContainer<?> mariaDbContainer() {
		return new MariaDBContainer<>("mariadb:latest");
	}

	public static void main(String[] args) {
		SpringApplication.from(PfccApplication::main).with(TestPfccApplication.class).run(args);
	}

}

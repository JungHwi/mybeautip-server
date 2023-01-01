package com.jocoos.mybeautip;

import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.jocoos.mybeautip.Teasdas.MY_SQL_CONTAINER;

@Testcontainers
public class OldTest {

    @DynamicPropertySource
    @DependsOn("teasdas")
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.master.hikari.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.master.hikari.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.master.hikari.jdbc-url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.slave.hikari.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.slave.hikari.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.slave.hikari.jdbc-url", MY_SQL_CONTAINER::getJdbcUrl);
    }
}

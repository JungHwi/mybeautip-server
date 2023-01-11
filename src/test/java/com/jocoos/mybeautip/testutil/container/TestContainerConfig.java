package com.jocoos.mybeautip.testutil.container;

import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.jocoos.mybeautip.testutil.container.MySqlContainer.MY_SQL_CONTAINER;

public class TestContainerConfig {

    @DynamicPropertySource
    @DependsOn("mySqlContainer2")
    private static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.master.hikari.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.master.hikari.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.master.hikari.jdbc-url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.slave.hikari.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.slave.hikari.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.slave.hikari.jdbc-url", MY_SQL_CONTAINER::getJdbcUrl);
    }

}

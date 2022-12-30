package com.jocoos.mybeautip;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class Teasdas implements BeforeAllCallback {
    private static final String MYSQL_DOCKER_VERSION = "mysql:8";

    @Container
    public MySQLContainer<?> MY_SQL_CONTAINER;


    @Override
    public void beforeAll(ExtensionContext context) {
        context.getConfigurationParameter("spring.profiles.active");
        MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse(MYSQL_DOCKER_VERSION)).withReuse(true);
        MY_SQL_CONTAINER.start();

        System.setProperty("spring.datasource.master.hikari.username", MY_SQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.master.hikari.password", MY_SQL_CONTAINER.getPassword());
        System.setProperty("spring.datasource.master.hikari.jdbc-url", MY_SQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.slave.hikari.username", MY_SQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.slave.hikari.password", MY_SQL_CONTAINER.getPassword());
        System.setProperty("spring.datasource.slave.hikari.jdbc-url", MY_SQL_CONTAINER.getJdbcUrl());
    }
}


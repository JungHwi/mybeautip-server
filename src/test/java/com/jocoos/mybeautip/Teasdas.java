package com.jocoos.mybeautip;

import org.springframework.boot.test.context.TestComponent;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestComponent
public class Teasdas {
    private static final String MYSQL_DOCKER_VERSION = "mysql:8";

    @Container
    public static final MySQLContainer<?> MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse(MYSQL_DOCKER_VERSION)).withReuse(true);
        MY_SQL_CONTAINER.start();
    }
}


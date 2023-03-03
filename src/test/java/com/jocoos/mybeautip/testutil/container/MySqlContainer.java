package com.jocoos.mybeautip.testutil.container;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class MySqlContainer {

    private static final String MYSQL_DOCKER_VERSION = "mysql:8";

    @Container
    public static final MySQLContainer<?> MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse(MYSQL_DOCKER_VERSION)).withReuse(true);
        MY_SQL_CONTAINER.start();
    }
}

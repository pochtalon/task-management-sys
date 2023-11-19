package mate.intro.repository;

import lombok.SneakyThrows;
import mate.intro.model.Project;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProjectRepositoryTest {
    @Autowired
    ProjectRepository projectRepository;
    private static final Long USER_ID = 2L;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource
    ) {
        clearTables(dataSource);
    }

    @SneakyThrows
    private static void clearTables(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/tasks/clear-tasks-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/projects/clear-projects-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/clear-users-table.sql")
            );
        }
    }

    @Test
    @Sql(
            scripts = "classpath:database/projects/add-two-projects-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/users/add-default-user-and-admin.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/tasks/add-three-tasks-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("Get all projects for authorized user")
    public void findByUserId_UserId_ReturnListOfProject() {
        List<Project> projects = projectRepository.findByUserId(USER_ID);
        assertEquals(2, projects.size());
    }
}

package mate.intro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.intro.dto.project.CreateProjectRequestDto;
import mate.intro.dto.project.ProjectDto;
import mate.intro.dto.project.UpdateProjectRequestDto;
import mate.intro.dto.task.TaskWithoutProjectDto;
import mate.intro.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    private static final String BEARER = "Bearer ";
    private static final Long PROJECT_ID_1 = 1L;
    private static final Long PROJECT_ID_2 = 2L;
    private static final String USER_EMAIL = "johnny@mail.com";
    private static final String NAME_FIRST = "Bird Counter";
    private static final String NAME_SECOND = "Day Counter";
    private static final String DESCRIPTION = "App for counting birds while you are studying";
    private static final String STATUS = "INITIATED";
    private static final String START_DATE = "2024-03-04";
    private static final String END_DATE = "2024-05-06";
    private static final String UP_NAME = "Name";
    private static final String UP_DESC = "Desc";
    private static final String UP_START = "2025-04-26";
    private static final String UP_END = "2025-11-15";
    private static final String UP_STATUS = "IN_PROGRESS";

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        clearTables(dataSource);
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create project")
    public void addProject_CreateProjectDto_ReturnProjectDto() throws Exception {
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto()
                .setName(NAME_FIRST)
                .setDescription(DESCRIPTION)
                .setStartDate(START_DATE)
                .setEndDate(END_DATE);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/projects")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ProjectDto expected = getProjectDto();
        ProjectDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
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
    @DisplayName("Get all projects for authenticated user")
    public void getProjects_ValidToken_ReturnListOfProjectDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/projects")
                        .header(HttpHeaders.AUTHORIZATION, BEARER
                                + jwtUtil.generateToken(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ProjectDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectDto[].class);
        assertEquals(2, actual.length);
    }

    @WithMockUser
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-two-projects-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("Get project by id")
    public void getProjectById_ProjectId_ReturnProjectDto() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/api/projects/" + PROJECT_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ProjectDto actual_first = objectMapper
                .readValue(result1.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(actual_first);
        assertEquals(NAME_FIRST, actual_first.getName());

        MvcResult result = mockMvc.perform(get("/api/projects/" + PROJECT_ID_2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ProjectDto actual_second = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(actual_second);
        assertEquals(NAME_SECOND, actual_second.getName());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
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
    @DisplayName("Get tasks for project by project id")
    public void getTasksForProject_ProjectId_ReturnListOfTaskWithoutProjectDto() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/api/projects/" + PROJECT_ID_1 + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TaskWithoutProjectDto[] actual_first = objectMapper
                .readValue(result1.getResponse().getContentAsString(), TaskWithoutProjectDto[].class);
        assertNotNull(actual_first);
        assertEquals(2, actual_first.length);

        MvcResult result2 = mockMvc.perform(get("/api/projects/" + PROJECT_ID_2 + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TaskWithoutProjectDto[] actual_second = objectMapper
                .readValue(result2.getResponse().getContentAsString(), TaskWithoutProjectDto[].class);
        assertNotNull(actual_second);
        assertEquals(1, actual_second.length);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-two-projects-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("Delete project by project id")
    public void deleteProject_ProjectId_Success() throws Exception {
        mockMvc.perform(delete("/api/projects/" + PROJECT_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(
            scripts = "classpath:database/projects/add-two-projects-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("Update project")
    public void updateProject_UpdateDtoAndProjectId_ReturnProjectDto() throws Exception {
        UpdateProjectRequestDto requestDto = getUpdateDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/api/projects/" + PROJECT_ID_1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProjectDto expected = new ProjectDto()
                .setName(UP_NAME)
                .setDescription(UP_DESC)
                .setStatus(UP_STATUS)
                .setStartDate(UP_START)
                .setEndDate(UP_END);
        ProjectDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    private UpdateProjectRequestDto getUpdateDto() {
        return new UpdateProjectRequestDto()
                .setName(UP_NAME)
                .setDescription(UP_DESC)
                .setStartDate(UP_START)
                .setEndDate(UP_END)
                .setStatus(UP_STATUS);
    }

    private ProjectDto getProjectDto() {
        return new ProjectDto()
                .setName(NAME_FIRST)
                .setDescription(DESCRIPTION)
                .setStatus(STATUS)
                .setStartDate(START_DATE)
                .setEndDate(END_DATE);
    }
}

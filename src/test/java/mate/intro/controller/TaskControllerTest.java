package mate.intro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import lombok.SneakyThrows;
import mate.intro.dto.task.CreateTaskRequestDto;
import mate.intro.dto.task.TaskDto;
import mate.intro.dto.task.UpdateTaskRequestDto;
import mate.intro.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
class TaskControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    private static final String BEARER = "Bearer ";
    private static final String USER_EMAIL = "johnny@mail.com";
    private static final String ADMIN_EMAIL = "admin@mail.com";
    private static final Long USER_TASK_ID = 1L;
    private static final Long ADMIN_TASK_ID = 2L;
    private static final Long UPDATED_TASK_ID = 3L;
    private static final String ADD_NAME = "New Task";
    private static final String NAME_ID_1 = "first counting";
    private static final String ADD_DESCRIPTION = "Desc for task";
    private static final String DESCRIPTION_ID_1 = "count birds on the tree";
    private static final String PRIORITY = "LOW";
    private static final String STATUS = "NOT_STARTED";
    private static final String ADD_DUE_DATE = "2024-06-08";
    private static final String DUE_DATE_1 = "2024-05-19";
    private static final Long PROJECT_ID = 2L;
    private static final String PROJECT_NAME = "Day Counter";
    private static final String ADMIN_NICK = "admin";
    private static final Long ASSIGNEE_ID = 2L;
    private static final String ASSIGNEE_NICK = "mnemonic";
    private static final String UP_NAME = "New name";
    private static final String UP_DESC = "Description";
    private static final String UP_PRIORITY = "HIGH";
    private static final String UP_STATUS = "IN_PROGRESS";
    private static final String UP_DATE = "2025-01-02";
    private static final Long UP_ASSIGNEE = 1L;


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

    @BeforeEach
    @SneakyThrows
    void addUsersAndProjects(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/projects/add-two-projects-to-db.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-default-user-and-admin.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/tasks/add-three-tasks-to-db.sql")
            );
        }
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
    @DisplayName("Create task")
    public void addProject_CreateTaskDto_ReturnTaskDto() throws Exception {
        CreateTaskRequestDto requestDto = getCreateRequest();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        TaskDto expected = getExpectedTaskDtoForAdding();
        TaskDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), TaskDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Get task by id, admin authorization")
    public void getTaskById_AdminAndTaskId_ReturnTaskDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks/" + USER_TASK_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER
                                + jwtUtil.generateToken(ADMIN_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        TaskDto expected = getExpectedTaskDtoFromDb();
        TaskDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), TaskDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Get task by id, valid user authorization")
    public void getTaskById_ValidUserAndTaskId_ReturnTaskDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks/" + USER_TASK_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER
                                + jwtUtil.generateToken(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        TaskDto expected = getExpectedTaskDtoFromDb();
        TaskDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), TaskDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Get task by id, task doesn't belong user")
    public void getTaskById_InvalidUserAndTaskId_ThrowException() throws Exception {
        Exception exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(get("/api/tasks/" + ADMIN_TASK_ID)
                                .header(HttpHeaders.AUTHORIZATION, BEARER
                                        + jwtUtil.generateToken(USER_EMAIL))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn());

        assertNotNull(exception);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update task by task id")
    public void updateProject_UpdateDtoAndTaskId_ReturnTaskDto() throws Exception {
        UpdateTaskRequestDto requestDto = getUpdateTaskDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/api/tasks/" + UPDATED_TASK_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TaskDto expected = getUpdatedTaskDto();
        TaskDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), TaskDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete task by task id")
    public void deleteProject_TaskId_Success() throws Exception {
        MvcResult delete = mockMvc.perform(delete("/api/tasks/" + USER_TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    private UpdateTaskRequestDto getUpdateTaskDto() {
        return new UpdateTaskRequestDto()
                .setName(UP_NAME)
                .setDescription(UP_DESC)
                .setStatus(UP_STATUS)
                .setPriority(UP_PRIORITY)
                .setDueDate(UP_DATE)
                .setProjectId(PROJECT_ID)
                .setAssigneeId(UP_ASSIGNEE);
    }

    private TaskDto getUpdatedTaskDto() {
        return new TaskDto()
                .setName(UP_NAME)
                .setDescription(UP_DESC)
                .setStatus(UP_STATUS)
                .setPriority(UP_PRIORITY)
                .setDueDate(UP_DATE)
                .setProjectName(PROJECT_NAME)
                .setUserNickname(ADMIN_NICK);
    }

    private TaskDto getExpectedTaskDtoFromDb() {
        return new TaskDto()
                .setName(NAME_ID_1)
                .setDescription(DESCRIPTION_ID_1)
                .setStatus(STATUS)
                .setPriority(PRIORITY)
                .setDueDate(DUE_DATE_1)
                .setProjectName(PROJECT_NAME)
                .setUserNickname(ASSIGNEE_NICK);
    }

    private TaskDto getExpectedTaskDtoForAdding() {
        return new TaskDto()
                .setName(ADD_NAME)
                .setDescription(ADD_DESCRIPTION)
                .setStatus(STATUS)
                .setPriority(PRIORITY)
                .setDueDate(ADD_DUE_DATE)
                .setProjectName(PROJECT_NAME)
                .setUserNickname(ASSIGNEE_NICK);
    }

    private CreateTaskRequestDto getCreateRequest() {
        return new CreateTaskRequestDto()
                .setName(ADD_NAME)
                .setDescription(ADD_DESCRIPTION)
                .setPriority(PRIORITY)
                .setDueDate(ADD_DUE_DATE)
                .setProjectId(PROJECT_ID)
                .setAssigneeId(ASSIGNEE_ID);
    }
}
































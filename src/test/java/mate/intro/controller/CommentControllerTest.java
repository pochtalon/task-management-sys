package mate.intro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.intro.dto.comment.CommentDto;
import mate.intro.dto.comment.CreateCommentRequestDto;
import mate.intro.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    private static final String BEARER = "Bearer ";
    private static final String USER_EMAIL = "johnny@mail.com";
    private static final String USER_NICK = "mnemonic";
    private static final Long TASK_ID = 1L;
    private static final String TEXT_CREATING = "New comment";

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

    @BeforeEach
    @SneakyThrows
    void addEntitiesToDataBase(@Autowired DataSource dataSource) {
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/comments/add-three-comments-to-db.sql")
            );
        }
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
                    new ClassPathResource("database/comments/clear-comments-table.sql")
            );
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
    @DisplayName("Create comment")
    public void addComment_CreateCommentDtoAndUser_ReturnCommentDto() throws Exception {
        CreateCommentRequestDto requestDto = getCreateCommentDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/comments")
                        .header(HttpHeaders.AUTHORIZATION, BEARER
                                + jwtUtil.generateToken(USER_EMAIL))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CommentDto expected = getNewCommentDto();
        CommentDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id", "timestamp");
    }

    @WithMockUser
    @Test
    @DisplayName("Get all comments for task")
    public void getCommentsForTask_TaskId_ReturnListOfCommentDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/comments?taskId=" + TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CommentDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CommentDto[].class);
        assertNotNull(actual);
        assertEquals(3, actual.length);
    }

    private CommentDto getNewCommentDto() {
        return new CommentDto()
                .setTaskId(TASK_ID)
                .setUser(USER_NICK)
                .setText(TEXT_CREATING);
    }

    private CreateCommentRequestDto getCreateCommentDto() {
        return new CreateCommentRequestDto()
                .setTaskId(TASK_ID)
                .setText(TEXT_CREATING);
    }
}
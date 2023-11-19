package mate.intro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.intro.dto.label.CreateLabelRequestDto;
import mate.intro.dto.label.LabelDto;
import mate.intro.dto.label.UpdateLabelRequestDto;
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
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LabelControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String NEW_COLOR = "RED";
    private static final String NEW_NAME = "NewLabel";
    private static final Long LABEL_ID_1 = 1L;
    private static final Long TASK_ID_1 = 1L;
    private static final Long TASK_ID_2 = 2L;
    private static final Long TASK_ID_3 = 3L;

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
                    new ClassPathResource("database/labels/add-three-labels-to-db.sql")
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
                    new ClassPathResource("database/labels/clear-labels-table.sql")
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new label")
    public void addLabel_CreateLabelDto_ReturnLabelDto() throws Exception {
        CreateLabelRequestDto requestDto = getCreateLabelDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/labels")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        LabelDto expected = new LabelDto()
                .setColor(NEW_COLOR)
                .setName(NEW_NAME);
        LabelDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), LabelDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id", "tasks");
        assertEquals(3, actual.getTasks().size());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get all labels")
    public void getLabels_AdminRole_ListOfLabelDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        LabelDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), LabelDto[].class);
        assertNotNull(actual);
        assertEquals(3, actual.length);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update label")
    public void updateLabel_LabelIdAndUpdateLabelDto_ReturnLabelDto() throws Exception {
        UpdateLabelRequestDto requestDto = getUpdateLabelDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/api/labels/" + LABEL_ID_1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        LabelDto expected = new LabelDto()
                .setColor(NEW_COLOR)
                .setName(NEW_NAME);
        LabelDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), LabelDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id", "tasks");
        assertEquals(3, actual.getTasks().size());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete label by label id")
    public void deleteLabel_LabelId_Success() throws Exception {
        mockMvc.perform(delete("/api/labels/" + LABEL_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    private UpdateLabelRequestDto getUpdateLabelDto() {
        return new UpdateLabelRequestDto()
                .setName(NEW_NAME)
                .setColor(NEW_COLOR)
                .setTaskIds(List.of(TASK_ID_1, TASK_ID_2, TASK_ID_3));
    }

    private CreateLabelRequestDto getCreateLabelDto() {
        return new CreateLabelRequestDto()
                .setColor(NEW_COLOR)
                .setName(NEW_NAME)
                .setTaskIds(List.of(TASK_ID_1, TASK_ID_2, TASK_ID_3));
    }
}

package mate.intro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.intro.dto.user.auth.UserLoginRequestDto;
import mate.intro.dto.user.auth.UserRegistrationRequestDto;
import mate.intro.dto.user.auth.UserResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String EMAIL = "johnny@mail.com";
    private static final String PASSWORD = "123456789";
    private static final String NICKNAME = "mnemonic";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        clearUserTable(dataSource);
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        clearUserTable(dataSource);
    }

    @SneakyThrows
    static void clearUserTable(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/clear-users-table.sql")
            );
        }
    }

    @Test
    @Sql(
            scripts = "classpath:database/users/add-default-user-and-admin.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("Login valid user")
    public void login_LoginRequestDto_Success() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @DisplayName("Login invalid user")
    public void login_InvalidLoginRequestDto_Status401() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @DisplayName("Register new user")
    public void register_RegistrationRequestDto_ReturnUserResponseDto() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail(EMAIL)
                .setPassword(PASSWORD)
                .setPasswordRepeat(PASSWORD)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setNickname(NICKNAME);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        UserResponseDto expected = new UserResponseDto()
                .setEmail(EMAIL);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }
}
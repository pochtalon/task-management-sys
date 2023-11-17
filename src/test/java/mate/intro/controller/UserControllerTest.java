package mate.intro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.intro.dto.role.RoleNameDto;
import mate.intro.dto.role.UpdateRolesRequestDto;
import mate.intro.dto.role.UpdateRolesResponseDto;
import mate.intro.dto.user.UserInfoDto;
import mate.intro.dto.user.UserUpdateInfoRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    private static final String BEARER = "Bearer ";
    private static final String ADMIN_EMAIL = "admin@mail.com";
    private static final String ADMIN_NICKNAME = "admin";
    private static final String ADMIN_FIRST_NAME = "Robert";
    private static final String ADMIN_LAST_NAME = "Heinlein";
    private static final Long USER_ID = 2L;
    private static final String USER_EMAIL = "johnny@mail.com";
    private static final String USER_NICKNAME = "mnemonic";
    private static final String USER_FIRST_NAME = "John";
    private static final String USER_LAST_NAME = "Smith";

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

    @BeforeEach
    @SneakyThrows
    void addUsers(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-default-user-and-admin.sql")
            );
        }
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update user's roles")
    public void updateRoles_UpdateDtoAndUserId_ReturnUpdateResponseDto() throws Exception {
        UpdateRolesRequestDto requestDto = new UpdateRolesRequestDto()
                .setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/api/users/" + USER_ID + "/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UpdateRolesResponseDto expected = getUpdateResponseDto();
        UpdateRolesResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), UpdateRolesResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get info about authorized user")
    public void getUserInfo_ValidToken_ReturnUserInfoDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, BEARER
                                + jwtUtil.generateToken(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserInfoDto expected = new UserInfoDto()
                .setNickname(USER_NICKNAME)
                .setEmail(USER_EMAIL)
                .setFirstName(USER_FIRST_NAME)
                .setLastName(USER_LAST_NAME);
        UserInfoDto actual =objectMapper.readValue(result.getResponse().getContentAsString(), UserInfoDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update user info")
    public void updateUserInfo_UpdateRequestDto_ReturnUserInfoDto() throws Exception {
        String nickname = "NewNick";
        String email = "newemail@mail.com";
        String firstName = "First";
        String lastName = "Last";
        String password = "Password";
        UserUpdateInfoRequestDto requestDto = new UserUpdateInfoRequestDto()
                .setEmail(email)
                .setNickname(nickname)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .setPasswordRepeat(password);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, BEARER
                                + jwtUtil.generateToken(USER_EMAIL))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserInfoDto expected = new UserInfoDto()
                .setNickname(nickname)
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName);
        UserInfoDto actual =objectMapper.readValue(result.getResponse().getContentAsString(), UserInfoDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    private UpdateRolesResponseDto getUpdateResponseDto() {
        RoleNameDto admin = new RoleNameDto().setName("ROLE_ADMIN");
        RoleNameDto user = new RoleNameDto().setName("ROLE_USER");
        return new UpdateRolesResponseDto()
                .setEmail(USER_EMAIL)
                .setRoleNames(Set.of(admin, user));
    }
}
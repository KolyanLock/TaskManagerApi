package org.kolyanlock.taskmanagerapi.controller;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kolyanlock.taskmanagerapi.TaskManagerApiApplication;
import org.kolyanlock.taskmanagerapi.dto.RoleDTO;
import org.kolyanlock.taskmanagerapi.dto.UserAuthDTO;
import org.kolyanlock.taskmanagerapi.dto.UserDTO;
import org.kolyanlock.taskmanagerapi.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.Set;


@SpringBootTest(classes = TaskManagerApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private UserFacade userFacade;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("SomeUser")
    private String someUsername;

    private String adminToken;
    private String someUserToken;

    @BeforeAll
    public void beforeAllSetup() throws RoleNotFoundException {
        UserAuthDTO admin = new UserAuthDTO(adminUsername, adminPassword);
        adminToken = userFacade.authenticateUserAndGetToken(admin);
        UserAuthDTO someUser = new UserAuthDTO(someUsername, "somePassword");
        userFacade.registerUser(someUser);
        someUserToken = userFacade.authenticateUserAndGetToken(someUser);
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({
            "admin, 200",
            "someUser, 403"
    })
    public void GetUsers(String key, int statusCode) {

        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;

        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/admin/users")
                .then().log().all()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().response();

        if (key.equals("admin")) {
            int contentSize = response.path("content.size()");
            Assertions.assertEquals(contentSize, 2);
        }
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({
            "admin, 1, 200",
            "admin, 2, 200",
            "admin, 3, 404",
            "someUser, 1, 403",
            "someUser, 2, 403",
            "someUser, 3, 403",
    })
    public void testGetUser(String key, Long userId,  int statusCode) {
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;
        String username = userId == 1 ? adminUsername : userId == 2 ? someUsername : null;

        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/admin/users/{userId}", userId)
                .then()
                .log().all()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().response();

        if (key.equals("admin") && statusCode == 200) {
            UserDTO userDTO = response.as(UserDTO.class);
            Assertions.assertEquals(userDTO.getUsername(), username);
        }
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({
            "someUser, 1, newAdmin, true, ROLE_ADMIN, 403",
            "someUser, 2, newsUser, true, ROLE_USER, 403",
            "admin, 1, newAdmin1, false, ROLE_ADMIN, 418",
            "admin, 1, newAdmin2, true, ROLE_USER, 418",
            "admin, 2, newsUser1, true, ROLE_USER, 200",
            "admin, 2, newsUser2, true, ROLE_FAIL, 400",
            "admin, 2, newsUser3, false, ROLE_ADMIN, 200",
            "admin, 2, someUser4, true, ROLE_ADMIN, 200",
            "admin, 2, someUser5, true, ROLE_ADMIN, 403",
            "admin, 1, newAdmin3, false, ROLE_USER, 200"
    })
    public void testUpdateUsername(String key, Long userId, String newUsername, boolean enabled, String newRole, int statusCode) {
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;

        RoleDTO role = new RoleDTO();
        role.setName(newRole);
        Set<RoleDTO> roles = new HashSet<>();
        roles.add(role);

        UserDTO updatedUser = new UserDTO(userId, newUsername , enabled, roles);

         Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .put("/api/admin/users/{userId}", userId)
                .then()
                .log().all()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().response();

         if (response.statusCode() == 200) {
             UserDTO userDTO = response.as(UserDTO.class);
             Assertions.assertEquals(userDTO.getUsername(), newUsername);
         }
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource({
            "someUser, 1, 403",
            "admin, 1, 403",
            "admin, 2, 200",
            "admin, 3, 404",
    })

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testDeleteUser(String key, Long userId,  int statusCode) throws RoleNotFoundException {
        registerSomeUserAndSetSomeUserToken();
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/admin/users/{userId}", userId)
                .then().log().all()
                .contentType(ContentType.JSON)
                .statusCode(statusCode);
    }

    public void registerSomeUserAndSetSomeUserToken() throws RoleNotFoundException {
        UserAuthDTO someUser = new UserAuthDTO(someUsername, "somePassword");
        userFacade.registerUser(someUser);
        someUserToken = userFacade.authenticateUserAndGetToken(someUser);
    }
}

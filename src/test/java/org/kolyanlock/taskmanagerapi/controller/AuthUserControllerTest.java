package org.kolyanlock.taskmanagerapi.controller;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.kolyanlock.taskmanagerapi.dto.UserAuthDTO;
import org.kolyanlock.taskmanagerapi.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
@ActiveProfiles("test")
public class AuthUserControllerTest {
    @Autowired
    private UserFacade userFacade;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Order(1)
    @Test
    public void testSuccessfulLogin() {
        UserAuthDTO userAuthDTO = new UserAuthDTO(adminUsername, adminPassword);

        RestAssured
                .given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(userAuthDTO)
                .post("/api/auth/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().response();
    }

    @Order(2)
    @Test
    public void testInvalidUsernameLogin() {
        UserAuthDTO userAuthDTO = new UserAuthDTO("InvalidUser", "invalidPassword");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(userAuthDTO)
                .when()
                .post("/api/auth/login")
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType(ContentType.JSON)
                .extract().response();
    }

    @Order(3)
    @Test
    public void testRegister() {
        UserAuthDTO userAuthDTO = new UserAuthDTO("SomeUser", "dkKksdf123");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(userAuthDTO)
                .when()
                .post("/api/auth/register")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().response();
    }

    @Order(4)
    @Test
    public void testRegisterExistingUser() {
        UserAuthDTO userAuthDTO = new UserAuthDTO(adminUsername, "djJdd#123");

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(userAuthDTO)
                .when()
                .post("/api/auth/register")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().response();
    }

    @Order(5)
    @Test
    public void testGetCurrentUserInfo() {
        UserAuthDTO userAuthDTO = new UserAuthDTO(adminUsername, adminPassword);
        String token = userFacade.authenticateUserAndGetToken(userAuthDTO);

        RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/auth/info")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().response();
    }
}

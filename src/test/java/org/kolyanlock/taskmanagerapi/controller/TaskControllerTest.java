package org.kolyanlock.taskmanagerapi.controller;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kolyanlock.taskmanagerapi.TaskManagerApiApplication;
import org.kolyanlock.taskmanagerapi.dto.TaskDTO;
import org.kolyanlock.taskmanagerapi.dto.TaskDetailDTO;
import org.kolyanlock.taskmanagerapi.dto.UserAuthDTO;
import org.kolyanlock.taskmanagerapi.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@SpringBootTest(classes = TaskManagerApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@ActiveProfiles("test")
public class TaskControllerTest {
    @Autowired
    private UserFacade userFacade;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("SomeUser")
    private String someUsername;

    private TaskDetailDTO adminTask;
    private TaskDetailDTO someUserTask;

    private String adminToken;
    private String someUserToken;

    @BeforeAll
    public void beforeAllSetup() {
        adminTask = new TaskDetailDTO();
        adminTask.setTitle("Задача админа");
        adminTask.setDescription("Описание задачи админа");

        someUserTask = new TaskDetailDTO();
        someUserTask.setTitle("Задача пользователя");
        someUserTask.setDescription("Описание задачи пользователя");

        UserAuthDTO admin = new UserAuthDTO(adminUsername, adminPassword);
        adminToken = userFacade.authenticateUserAndGetToken(admin);
    }

    @BeforeEach
    public void beforeEachSetup() throws RoleNotFoundException {
        UserAuthDTO someUser = new UserAuthDTO(someUsername, "somePassword");
        userFacade.registerUser(someUser);
        someUserToken = userFacade.authenticateUserAndGetToken(someUser);
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({
            "admin",
            "someUser"
    })
    @DirtiesContext
    public void createTask(String key) {
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;
        TaskDetailDTO task = key.equals("admin") ? adminTask : key.equals("someUser") ? someUserTask : null;

        RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(task)
                .when()
                .post("/api/tasks")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .extract().response();
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({
            "admin",
            "someUser"
    })
    @DirtiesContext
    public void getTasks(String key) {
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;
        String title = key.equals("admin") ? adminTask.getTitle() : key.equals("someUser") ? someUserTask.getTitle() : null;

        createTasksForTest();

        List<TaskDTO> tasks = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/tasks")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().response().jsonPath().getList("content", TaskDTO.class);

        Assertions.assertEquals(tasks.size(), 1);
        Assertions.assertEquals(tasks.get(0).getTitle(), title);
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({
            "admin, 1, 200",
            "admin, 2, 404",
            "someUser, 1, 404",
            "someUser, 2, 200"
    })
    @DirtiesContext
    public void getTaskById(String key, Long taskId, int statusCode) {
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;
        String title = taskId == 1 ? adminTask.getTitle() : taskId == 2 ? someUserTask.getTitle() : null;

        createTasksForTest();

        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/tasks/{taskId}", taskId)
                .then().log().all()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().response();

        if (statusCode == 200) {
            TaskDetailDTO task = response.as(TaskDetailDTO.class);
            Assertions.assertEquals(task.getTitle(), title);
        }
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource({
            "admin, 1, 200",
            "admin, 2, 404",
            "someUser, 1, 404",
            "someUser, 2, 200"
    })
    @DirtiesContext
    public void updateTask(String key, Long taskId, int statusCode) {
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;
        String newTitle = key.equals("admin") ? "Новая задача админа" : key.equals("someUser") ? "Новая задача пользователя" : null;
        TaskDetailDTO task = taskId == 1 ? adminTask : taskId == 2 ? someUserTask : null;

        createTasksForTest();

        assert task != null;
        task.setTitle(newTitle);

        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(task)
                .when()
                .put("/api/tasks/{taskId}", taskId)
                .then().log().all()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().response();

        if (statusCode == 200) {
            TaskDetailDTO updatedTask = response.as(TaskDetailDTO.class);
            Assertions.assertEquals(updatedTask.getTitle(), newTitle);
        }
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({
            "admin, 1, 200",
            "admin, 2, 404",
            "someUser, 1, 404",
            "someUser, 2, 200"
    })
    @DirtiesContext
    public void deleteTask(String key, Long taskId, int statusCode) {
        String token = key.equals("admin") ? adminToken : key.equals("someUser") ? someUserToken : null;
        String title = taskId == 1 ? adminTask.getTitle() : taskId == 2 ? someUserTask.getTitle() : null;

        createTasksForTest();

        Response response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/tasks/{taskId}", taskId)
                .then().log().all()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().response();

        if (statusCode == 200) {
            TaskDetailDTO task = response.as(TaskDetailDTO.class);
            Assertions.assertEquals(task.getTitle(), title);
        }
    }

    private void createTasksForTest() {
        RestAssured.given().header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(adminTask)
                .post("/api/tasks");

        RestAssured.given().header("Authorization", "Bearer " + someUserToken)
                .contentType(ContentType.JSON)
                .body(someUserTask)
                .post("/api/tasks");
    }
}

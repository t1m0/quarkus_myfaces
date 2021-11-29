package com.t1m0.quarkus.web.rest;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.ws.rs.core.Response;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class UserEndpointTest {

    @Test
    public void givenExistingResourceWhenGetWithUUIThenExpectedResourceIsReturned() throws URISyntaxException, IOException {
        given()
                .when().get("/users/4ed3e496-2112-11ec-86e2-1ff4cb7ba1b6")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(equalTo(loadBody("single.json")));
    }

    @Test
    public void givenNotExistingResourceWhenGetWithUUIThen404IsReturned() throws URISyntaxException, IOException {
        given()
                .when().get("/users/3333-2112-22-11-NO")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void givenLimit5AndNoPageWhenGetThenExpectedResourcesAreReturned() throws URISyntaxException, IOException {
        given()
                .when().get("/users?limit=5")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(equalTo(loadBody("multiple_page_1.json")));
    }

    @Test
    public void givenLimit5AndPage5WhenGetThenExpectedResourcesAreReturned() throws URISyntaxException, IOException {
        given()
                .when().get("/users?limit=5&page=5")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(equalTo(loadBody("multiple_page_5.json")));
    }

    @Test
    public void givenNotExistingPageNumberWhenGetThenEmptyPageIsReturnedReturned() throws URISyntaxException, IOException {
        given()
                .when().get("/users?limit=1000&page=5")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(equalTo(loadBody("empty_page.json")));
    }

    @Test
    public void givenNotExistingUserWhenPostThenNewUserWithUUUIDisReturned() throws URISyntaxException, IOException {
        given()
                .when().body(loadBody("new_user_request.json")).contentType(ContentType.JSON).post("/users")
                .then().assertThat()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body("firstName", equalTo("John"),
                        "lastName", equalTo("Doe"),
                        "uuid", notNullValue());
    }

    @Test
    public void givenChangedAddressWhenPutThenUpdatedUserIsReturned() throws URISyntaxException, IOException, SQLException {
        String userUpdateJson = loadBody("update_user.json");
        given()
                .when().body(userUpdateJson).contentType(ContentType.JSON).put("/users/4ed3e572-2112-11ec-86e8-bb873bfa6bf7")
                .then().assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(equalTo(userUpdateJson));
    }

    @Test
    public void givenDifferentIdInPathAndBodyWhenPutThenBadRequestIsReturned() throws URISyntaxException, IOException {
        given()
                .when().body(loadBody("update_user.json")).contentType(ContentType.JSON).put("/users/4ed3e572-2112-11ec-86e8-333")
                .then().assertThat()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void givenUnknownUUIDWhenPutThenNotFoundIsReturned() throws URISyntaxException, IOException {
        given()
                .when().body(loadBody("update_user_unknown_uuid.json")).contentType(ContentType.JSON).put("/users/4ed3e572-2112-11ec-86e8-333")
                .then().assertThat()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    private String loadBody(String fileName) throws URISyntaxException, IOException {
        URI uri = UserEndpointTest.class.getClassLoader().getResource(fileName).toURI();
        return FileUtils.readFileToString(new File(uri), StandardCharsets.UTF_8);
    }

}
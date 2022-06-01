package com.t1m0.quarkus.web;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.t1m0.quarkus.web.model.User;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class UserServiceClientTest {

    @Test
    public void givenServerResponseWhenFindThenExpectedUserIsReturned(WireMockRuntimeInfo runtimeInfo) throws UserServiceClientException, URISyntaxException, IOException {
        stubFor(get(UserServiceClient.ENDPOINT + "/EXISTING-UUID").willReturn(aResponse().withBody(loadBody("user.json")).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withStatus(Response.Status.OK.getStatusCode())));
        User user = getClient(runtimeInfo).find("EXISTING-UUID");
        assertUser(user);
        verify(getRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT + "/EXISTING-UUID")));
    }

    @Test
    public void given404WhenFindWithThenExceptionIsThrown(WireMockRuntimeInfo runtimeInfo) throws UserServiceClientException, URISyntaxException, IOException {
        stubFor(get(UserServiceClient.ENDPOINT + "/NOT-EXISTING-UUID").willReturn(notFound()));
        assertThrows(UserServiceClientException.class, () -> getClient(runtimeInfo).find("NOT-EXISTING-UUID"), "404 Not Found");
        verify(getRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT + "/NOT-EXISTING-UUID")));
    }

    @Test
    public void givenResponseWithResultsWhenFindAllThenExpectedPageIsReturned(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException, UserServiceClientException {
        stubFor(get(UserServiceClient.ENDPOINT + "?limit=5&page=0").willReturn(aResponse().withBody(loadBody("page.json")).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withStatus(Response.Status.OK.getStatusCode())));
        Page<User> page = getClient(runtimeInfo).findAll(5, 0);
        assertEquals(0, page.getCurrentPage());
        assertEquals(100, page.getTotalItemCount());
        assertEquals(5, page.getEntryLimit());
        assertEquals(5, page.getEntries().size());
        verify(getRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT + "?limit=5&page=0")));
    }

    @Test
    public void givenResponseWithoutResultsWhenFindAllThenEmptyPageIsReturned(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException, UserServiceClientException {
        stubFor(get(UserServiceClient.ENDPOINT + "?limit=1000&page=5").willReturn(aResponse().withBody(loadBody("empty_page.json")).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withStatus(Response.Status.OK.getStatusCode())));
        Page<User> page = getClient(runtimeInfo).findAll(1000, 5);
        assertEquals(5, page.getCurrentPage());
        assertEquals(101, page.getTotalItemCount());
        assertEquals(1000, page.getEntryLimit());
        assertEquals(0, page.getEntries().size());
        verify(getRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT + "?limit=1000&page=5")));
    }

    @Test
    public void givenUserWhenCreateThenWebServiceIsCalled(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException, UserServiceClientException {
        stubFor(post(UserServiceClient.ENDPOINT).willReturn(aResponse().withBody(loadBody("user.json")).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withStatus(Response.Status.OK.getStatusCode())));
        User user = getClient(runtimeInfo).create(new User());
        assertUser(user);
        verify(postRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT)));
    }

    @Test
    public void givenUserWithUUIDWhenCreateThenWebServiceIsNotCalled(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException, UserServiceClientException {
        assertThrows(UserServiceClientException.class, () -> getClient(runtimeInfo).create(null), UserServiceClient.UUID_NOT_NULL_MSG);
        verify(0, postRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT)));
    }

    @Test
    public void givenNullWhenCreateThenWebServiceIsNotCalled(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException {
        assertThrows(UserServiceClientException.class, () -> getClient(runtimeInfo).create(null), UserServiceClient.USER_NULL_MSG);
        verify(0, postRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT)));
    }

    @Test
    public void givenUserWithUUIDWhenUpdateThenWebServiceIsCalled(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException, UserServiceClientException {
        stubFor(put(UserServiceClient.ENDPOINT + "/SOME-UUID").willReturn(aResponse().withBody(loadBody("user.json")).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withStatus(Response.Status.OK.getStatusCode())));
        User inputUser = new User();
        inputUser.setUuid("SOME-UUID");
        User user = getClient(runtimeInfo).update(inputUser);
        assertUser(user);
        verify(putRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT + "/SOME-UUID")));
    }

    @Test
    public void givenUserWithoutUUIDWhenUpdateThenWebServiceNotIsCalled(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException, UserServiceClientException {
        stubFor(put(UserServiceClient.ENDPOINT).willReturn(aResponse().withBody(loadBody("user.json")).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withStatus(Response.Status.OK.getStatusCode())));
        assertThrows(UserServiceClientException.class, () -> getClient(runtimeInfo).update(new User()), UserServiceClient.UUID_NULL_MSG);
        verify(0, putRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT)));
    }

    @Test
    public void givenNullWhenUpdateThenWebServiceIsNotCalled(WireMockRuntimeInfo runtimeInfo) throws URISyntaxException, IOException {
        stubFor(put(UserServiceClient.ENDPOINT).willReturn(aResponse().withBody(loadBody("user.json")).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).withStatus(Response.Status.OK.getStatusCode())));
        assertThrows(UserServiceClientException.class, () -> getClient(runtimeInfo).update(null), UserServiceClient.USER_NULL_MSG);
        verify(0, putRequestedFor(urlEqualTo(UserServiceClient.ENDPOINT)));
    }

    private void assertUser(User user) {
        assertEquals("John", user.getFirstName());
        assertEquals("New City", user.getAddress().getCity());
    }

    private String loadBody(String fileName) throws URISyntaxException, IOException {
        URI uri = UserServiceClient.class.getClassLoader().getResource(fileName).toURI();
        return FileUtils.readFileToString(new File(uri), StandardCharsets.UTF_8);
    }

    private UserServiceClient getClient(WireMockRuntimeInfo runtimeInfo) {
        return new UserServiceClient(runtimeInfo.getHttpBaseUrl());
    }

}

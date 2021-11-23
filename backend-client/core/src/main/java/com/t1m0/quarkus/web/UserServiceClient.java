package com.t1m0.quarkus.web;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.t1m0.quarkus.web.model.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

@ApplicationScoped
public class UserServiceClient {

    private final String backendUrl;

    @Inject
    public UserServiceClient(@ConfigProperty(name = "com.t1m0.quarkus.web.backend") String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public User create(User user) throws UserServiceClientException {
        return callBackend(backendUrl, Response.Status.OK, new GenericType<User>() {
        }, (webTarget ->
                webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON), Response.class)
        ));
    }

    public User update(User user) throws UserServiceClientException {
        String actualUrl = backendUrl + "/" + user.getUuid();
        return callBackend(actualUrl, Response.Status.OK, new GenericType<User>() {
        }, (webTarget ->
                webTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(user, MediaType.APPLICATION_JSON), Response.class)
        ));
    }

    public User find(String uuid) throws UserServiceClientException {
        String actualUrl = backendUrl + "/" + uuid;
        return callBackend(actualUrl, Response.Status.OK, new GenericType<User>() {
        }, (webTarget ->
                webTarget.request(MediaType.APPLICATION_JSON).get(Response.class)
        ));
    }

    public Page<User> findAll(int limit, int page) throws UserServiceClientException {
        String actualUrl = backendUrl + "?limit=" + limit + "&page=" + page;
        return callBackend(actualUrl, Response.Status.OK, new GenericType<Page<User>>() {
        }, (webTarget ->
                webTarget.request(MediaType.APPLICATION_JSON).get(Response.class)
        ));
    }

    private <T> T callBackend(String url, Response.Status expectedStatus, GenericType<T> type, UserServiceCall userServiceCall) throws UserServiceClientException {
        Client client = ClientBuilder.newClient();
        ResteasyWebTarget webTarget = (ResteasyWebTarget) client.target(url);
        Response response = userServiceCall.call(webTarget);
        validateResponse(response, expectedStatus);
        return response.readEntity(type);
    }

    private void validateResponse(Response response, Response.Status expectedStatus) throws UserServiceClientException {
        if (response.getStatus() != expectedStatus.getStatusCode()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(response.getStatus());
            stringBuilder.append(" ");
            stringBuilder.append(response.getStatusInfo().getReasonPhrase());
            if (response.hasEntity()) {
                stringBuilder.append("\n");
                stringBuilder.append(response.readEntity(String.class));
                throw new UserServiceClientException(stringBuilder.toString());
            } else {
                throw new UserServiceClientException(stringBuilder.toString());
            }
        }
    }

    private interface UserServiceCall {
        Response call(WebTarget webTarget);
    }
}

package com.t1m0.quarkus.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.t1m0.quarkus.web.UserServiceClient;
import com.t1m0.quarkus.web.UserServiceClientException;
import com.t1m0.quarkus.web.model.User;

@RequestScoped
@Named("simpleView")
public class SimpleView {

    private final UserServiceClient userServiceClient;
    private final List<User> users = new ArrayList<>();

    @Inject
    public SimpleView(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @PostConstruct
    public void postConstruct() throws UserServiceClientException {
        users.addAll(userServiceClient.findAll(200, 0).getEntries());
    }

    public List<User> getUsers() {
        return users;
    }
}

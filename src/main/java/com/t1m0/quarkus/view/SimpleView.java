package com.t1m0.quarkus.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.t1m0.quarkus.model.User;
import com.t1m0.quarkus.persistence.UserRepository;

@ViewScoped
@Named("simpleView")
public class SimpleView {

    private final UserRepository userRepository;
    private final List<User> users = new ArrayList<>();

    @Inject
    public SimpleView(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void postConstruct() {
        users.addAll(userRepository.findAll());
    }

    public List<User> getUsers() {
        return users;
    }
}

package com.t1m0.quarkus.web.rest;


import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import com.t1m0.quarkus.web.Page;
import com.t1m0.quarkus.web.UserService;
import com.t1m0.quarkus.web.model.User;
import com.t1m0.quarkus.web.persistence.UserRepository;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class UserEndpoint implements UserService {

    public static final String ERROR_NO_UUID = "UUID not provided!";
    public static final String ERROR_UUID_MISMATCH = "Path and body UUID do not match!";
    public static final String ERROR_UUID_ON_CREATE = "Creating a user with an UUID is not allowed! The UUID will be generated by the backend.";
    public static final String ERROR_NOT_FOUND = "No user for uuid %s found!";

    @Inject
    private UserRepository userRepository;

    @Override
    public Response save(User user) {
        if (StringUtils.isNotEmpty(user.getUuid())) {
            throw new ForbiddenException(ERROR_UUID_ON_CREATE);
        }
        User createdUser = userRepository.save(user);
        return Response.status(201).entity(createdUser).build();
    }

    @Override
    public User save(String uuid, User user) {
        if(StringUtils.isEmpty(uuid)) {
            throw new BadRequestException(ERROR_NO_UUID);
        } else if(!uuid.equals(user.getUuid())) {
            throw new BadRequestException(ERROR_UUID_MISMATCH);
        } else if (userRepository.find(uuid) == null) {
            throw new NotFoundException(String.format(ERROR_NOT_FOUND, uuid));
        }
        return userRepository.save(user);
    }

    @Override
    public void delete(String uuid) {
        User user = userRepository.find(uuid);
        if(user == null) {
            throw new NotFoundException(String.format(ERROR_NOT_FOUND, uuid));
        }
        userRepository.delete(user);
    }

    @Override
    public User find(String uuid) {
        User user = userRepository.find(uuid);
        if(user == null) {
            throw new NotFoundException(String.format(ERROR_NOT_FOUND, uuid));
        }
        return user;
    }

    @Override
    public Page<User> findAll(int limit, int page) {
        return userRepository.findAll(limit, page, new HashMap<>(), new HashMap<>());
    }
}

package com.t1m0.quarkus.view;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.t1m0.quarkus.model.User;
import com.t1m0.quarkus.persistence.Page;
import com.t1m0.quarkus.persistence.UserRepository;
import org.apache.commons.lang3.StringUtils;

@ViewScoped
@Named("simplePaginationView")
public class SimplePaginationView {
    private static final int DEFAULT_LIMIT = 10;
    private static final int DEFAULT_PAGE = 1;

    @Inject
    private UserRepository userRepository;
    @Inject
    private HttpServletRequest httpServletRequest;

    private Page<User> currentPage = null;

    @PostConstruct
    public void postConstruct() {
        int limit = getIntParam("limit", DEFAULT_LIMIT);
        int page = getIntParam("page", DEFAULT_PAGE);
        currentPage = userRepository.findAll(limit, page - 1, new HashMap<>(), new HashMap<>());
    }

    public Page<User> getCurrentPage() {
        return currentPage;
    }

    protected int getIntParam(String parameter, int defaultValue) {
        String paramValue = httpServletRequest.getParameter(parameter);
        return StringUtils.isNumeric(paramValue) ? Integer.parseInt(paramValue) : defaultValue;
    }
}

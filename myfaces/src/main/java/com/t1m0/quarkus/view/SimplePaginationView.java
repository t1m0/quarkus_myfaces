package com.t1m0.quarkus.view;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.t1m0.quarkus.web.Page;
import com.t1m0.quarkus.web.UserServiceClient;
import com.t1m0.quarkus.web.UserServiceClientException;
import com.t1m0.quarkus.web.model.User;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
@Named("simplePaginationView")
public class SimplePaginationView {
    private static final int DEFAULT_LIMIT = 10;
    private static final int DEFAULT_PAGE = 1;

    @Inject
    private UserServiceClient userServiceClient;
    @Inject
    private HttpServletRequest httpServletRequest;

    private Page<User> currentPage = null;

    @PostConstruct
    public void postConstruct() throws UserServiceClientException {
        int limit = getIntParam("limit", DEFAULT_LIMIT);
        int page = getIntParam("page", DEFAULT_PAGE);
        currentPage = userServiceClient.findAll(limit, page - 1);
    }

    public Page<User> getCurrentPage() {
        return currentPage;
    }

    protected int getIntParam(String parameter, int defaultValue) {
        String paramValue = httpServletRequest.getParameter(parameter);
        return StringUtils.isNumeric(paramValue) ? Integer.parseInt(paramValue) : defaultValue;
    }
}

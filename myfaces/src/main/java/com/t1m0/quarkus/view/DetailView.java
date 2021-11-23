package com.t1m0.quarkus.view;


import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.t1m0.quarkus.web.UserServiceClient;
import com.t1m0.quarkus.web.UserServiceClientException;
import com.t1m0.quarkus.web.model.User;
import com.t1m0.quarkus.web.model.UserState;
import org.apache.commons.lang3.StringUtils;

@ViewScoped
@Named("detailView")
public class DetailView {

    @Inject
    private UserServiceClient userServiceClient;
    @Inject
    private HttpServletRequest httpServletRequest;

    private User user;

    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @PostConstruct
    public void postConstruct() throws UserServiceClientException {
        String uuid = httpServletRequest.getParameter("uuid");
        if (StringUtils.isNotEmpty(uuid)) {
            user = userServiceClient.find(uuid);
        } else {
            user = new User();
        }
    }

    public void save() throws UserServiceClientException {
        user = userServiceClient.update(user);
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add("postSave();");
    }

    public void lock() throws UserServiceClientException {
        updateUserState(UserState.Locked);
    }

    public void unlock() throws UserServiceClientException {
        updateUserState(UserState.Active);
    }

    public void confirm() throws UserServiceClientException {
        updateUserState(UserState.Active);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void updateUserState(UserState state) throws UserServiceClientException {
        user.setState(state);
        user = userServiceClient.update(user);
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add("showCorrectActionButton();");
    }

}

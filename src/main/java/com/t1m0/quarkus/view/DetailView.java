package com.t1m0.quarkus.view;


import javax.annotation.PostConstruct;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.t1m0.quarkus.model.User;
import com.t1m0.quarkus.model.UserState;
import com.t1m0.quarkus.persistence.UserRepository;
import org.apache.commons.lang3.StringUtils;

@ViewScoped
@Named("detailView")
public class DetailView {

    @Inject
    private UserRepository userRepository;
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
    public void postConstruct() {
        String uuid = httpServletRequest.getParameter("uuid");
        if(StringUtils.isNotEmpty(uuid)){
            user = userRepository.find(uuid);
        } else {
            user = new User();
        }
    }

    public void save() {
        user = userRepository.save(user);
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add("postSave();");
    }

    public void lock() {
        updateUserState(UserState.Locked);
    }

    public void unlock() {
        updateUserState(UserState.Active);
    }

    public void confirm() {
        updateUserState(UserState.Active);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void updateUserState(UserState state) {
        user.setState(state);
        user = userRepository.save(user);
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts().add("showCorrectActionButton();");
    }

}

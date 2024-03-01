package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.ForgottenPasswordUI;
import com.telerikacademy.web.carpooling.models.User;
import org.springframework.stereotype.Component;

@Component
public class UIMapper {

    public ForgottenPasswordUI toFPUI (User user, String UI) {
        ForgottenPasswordUI forgottenPasswordUI = new ForgottenPasswordUI();
        forgottenPasswordUI.setUser(user);
        forgottenPasswordUI.setUI(UI);
        return forgottenPasswordUI;
    }
}

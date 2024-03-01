package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

@Entity
@Table(name = "forgottenpass_uis")
public class ForgottenPasswordUI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "UI")
    private String UI;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUI() {
        return UI;
    }

    public void setUI(String UI) {
        this.UI = UI;
    }
}

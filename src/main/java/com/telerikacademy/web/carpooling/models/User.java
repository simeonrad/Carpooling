package com.telerikacademy.web.carpooling.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username", unique = true, nullable = false, length = 20)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false, length = 10)
    private String phoneNumber;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "user_role")
    private Role role;
    @OneToOne(mappedBy = "user")
    private UserBlock userBlocks;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private IsDeleted isDeletedRecord;

    @OneToOne(mappedBy = "user")
    private ForgottenPasswordUI forgottenPasswordUI;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id")
    private List<Feedback> feedbacks;

    public User() {
    }

    public User(String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }
    public double getFeedback(){
        int sum = 0;
        for (Feedback feedback : feedbacks) {
            sum += feedback.getRating();
        }
        BigDecimal average = BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(feedbacks.size()), 2, RoundingMode.HALF_UP);
        return average.doubleValue();
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public boolean isBlocked() {
        return userBlocks != null;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserBlock getUserBlocks() {
        return userBlocks;
    }

    public void setUserBlocks(UserBlock userBlocks) {
        this.userBlocks = userBlocks;
    }

    public IsDeleted getIsDeletedRecord() {
        return isDeletedRecord;
    }

    public void setIsDeletedRecord(IsDeleted isDeletedRecord) {
        this.isDeletedRecord = isDeletedRecord;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && username.equals(user.username);
    }

    public boolean isAdmin() {
        return role.getName().equals("Admin");
    }

    public ForgottenPasswordUI getForgottenPasswordUI() {
        return forgottenPasswordUI;
    }

    public void setForgottenPasswordUI(ForgottenPasswordUI forgottenPasswordUI) {
        this.forgottenPasswordUI = forgottenPasswordUI;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

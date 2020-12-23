package com.example.motioner.domain.entity;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String mobile;

    private String password;

    private String username;

    private String status;

    public User() {}

    public User(Long id, String email, String mobile, String password, String username, String status) {
        this.id = id;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.username = username;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}

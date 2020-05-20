package com.kivilcimeray.accounting.model.api;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(name = "USER")
public class User {
    @Id
    UUID id;

    @Column(name = "username", unique = true)
    String username;

    String password;

    Boolean enabled = true;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<UserAuthority> userAuthorities = new ArrayList<>();

    public User() {
    }

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.enabled = user.enabled;
        this.userAuthorities = new ArrayList<>(user.userAuthorities);
    }

    public User(UUID id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public void addAuthority(String authority) {
        this.userAuthorities.add(new UserAuthority(this, authority));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
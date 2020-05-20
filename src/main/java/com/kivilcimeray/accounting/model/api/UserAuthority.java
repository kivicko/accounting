package com.kivilcimeray.accounting.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "authorities")
public class UserAuthority {

    @Id
    UUID id;

    @ManyToOne
    User user;

    String authority;

    public UserAuthority(User user, String authority) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "UserAuthority{" +
                "id=" + id +
                ", user=" + user;
    }
}
package com.reading.is.good.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AuthorizationEntity extends BaseEntity<String> {

    private String auth;
    @ManyToMany(fetch = FetchType.EAGER, cascade = ALL)
    @JoinTable(
            name = "user_authorization",
            joinColumns = {@JoinColumn(name = "authorization_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<UserEntity> users;

}

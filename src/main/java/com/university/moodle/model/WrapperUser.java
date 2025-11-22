package com.university.moodle.model;

import lombok.Getter;

@Getter
public class WrapperUser {
    private final User user;

    public WrapperUser(User user) {
        this.user = user;
    }
}

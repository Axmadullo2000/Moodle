package com.university.moodle.model;

import com.university.moodle.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    private String id;
    private String userName;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
    private boolean active = true;
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at  = LocalDateTime.now();

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}

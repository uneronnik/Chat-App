package com.example.chatapp.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
@Schema(description = "Пользователь")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Имя пользователя", example = "nikita")
    @Column(unique = true, nullable = false)
    private String username;
    @Schema(description = "Email", example = "nikita@example.com")
    @Column(unique = true, nullable = false)
    private String email;
    @Schema(description = "Пароль", example = "qwerty123")
    @Column(nullable = false)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public User(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected User() {

    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }
    @JsonIgnore
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(username, user.username)
                && Objects.equals(email, user.email);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}

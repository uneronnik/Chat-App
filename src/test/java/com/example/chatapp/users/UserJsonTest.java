package com.example.chatapp.users;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
@ActiveProfiles("test")
public class UserJsonTest {
    @Autowired
    private JacksonTester<User> json;

    @Autowired
    private JacksonTester<User[]> jsonList;

    private User[] users;

    @BeforeEach
    void setUp() {
        users = Arrays.array(
                new User(1L, "nikita", "nikita@example.com", "123"),
                new User(2L, "tanya", "tanya@example.com", "qwerty"),
                new User(3L, "alya", "alya@example.com", "zxc")
        );
    }

    @Test
    void userSerializationTest() throws IOException {
        User user = new User(1L, "nikita","nikita@example.com", "123");
        JsonContent<User> jsonUser = json.write(user);
        assertThat(jsonUser).isStrictlyEqualToJson("single.json");

        assertThat(jsonUser).hasJsonPath("@.id");
        assertThat(jsonUser).extractingJsonPathNumberValue("@.id").isEqualTo(1);

        assertThat(jsonUser).hasJsonPath("@.username");
        assertThat(jsonUser).extractingJsonPathStringValue("@.username").isEqualTo("nikita");

        assertThat(jsonUser).hasJsonPath("@.email");
        assertThat(jsonUser).extractingJsonPathStringValue("@.email").isEqualTo("nikita@example.com");

    }

    @Test
    void userDeserializationTest() throws IOException {
        String content = """
            {
                "id": 1,
                "username": "nikita",
                "email": "nikita@example.com"
            }
            """;

        User expected = new User(1L, "nikita", "nikita@example.com", "123");

        assertThat(json.parse(content)).isEqualTo(expected);
        assertThat(json.parseObject(content).getId()).isEqualTo(1L);
        assertThat(json.parseObject(content).getUsername()).isEqualTo("nikita");
        assertThat(json.parseObject(content).getEmail()).isEqualTo("nikita@example.com");
    }

    @Test
    void userListSerializationTest() throws IOException {
        assertThat(jsonList.write(users)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void userListDeserializationTest() throws IOException {
        String expected = """
            [
                {"id": 1, "username": "nikita", "email": "nikita@example.com"},
                {"id": 2, "username": "tanya", "email": "tanya@example.com"},
                {"id": 3, "username": "alya", "email": "alya@example.com"}
            ]
            """;
        assertThat(jsonList.parse(expected)).isEqualTo(users);
    }
}

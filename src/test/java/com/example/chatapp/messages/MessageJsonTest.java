package com.example.chatapp.messages;

import com.example.chatapp.users.User;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ActiveProfiles("test")
public class MessageJsonTest {
    @Autowired
    private JacksonTester<Message> json;

    @Autowired
    private JacksonTester<Message[]> jsonList;

    private Message[] messages;

    private static final LocalDateTime TIME_1 = LocalDateTime.of(2025, 3, 19, 10, 30, 0);
    private static final LocalDateTime TIME_2 = LocalDateTime.of(2025, 3, 19, 10, 31, 0);
    private static final LocalDateTime TIME_3 = LocalDateTime.of(2025, 3, 19, 10, 32, 0);

    @BeforeEach
    void setUp() {
        User nikita = new User(1L, "nikita", "nikita@example.com", "123");
        User tanya = new User(2L, "tanya", "tanya@example.com", "qwerty");

        messages = Arrays.array(
                new Message(1L, "Привет!", nikita, tanya),
                new Message(2L, "Привет, как дела?", tanya, nikita),
                new Message(3L, "Отлично!", nikita, tanya)
        );
    }

    @Test
    void messageSerializationTest() throws IOException {
        User nikita = new User(1L, "nikita", "nikita@example.com", "123");
        User tanya = new User(2L, "tanya", "tanya@example.com", "qwerty");

        Message message = new Message(1L, "Привет!", nikita, tanya);
        JsonContent<Message> jsonMessage = json.write(message);

        assertThat(jsonMessage).hasJsonPath("@.id");
        assertThat(jsonMessage).extractingJsonPathNumberValue("@.id").isEqualTo(1);

        assertThat(jsonMessage).hasJsonPath("@.content");
        assertThat(jsonMessage).extractingJsonPathStringValue("@.content").isEqualTo("Привет!");

        assertThat(jsonMessage).hasJsonPath("@.owner.id");
        assertThat(jsonMessage).extractingJsonPathNumberValue("@.owner.id").isEqualTo(1);

        assertThat(jsonMessage).hasJsonPath("@.owner.username");
        assertThat(jsonMessage).extractingJsonPathStringValue("@.owner.username").isEqualTo("nikita");

        assertThat(jsonMessage).hasJsonPath("@.receiver.id");
        assertThat(jsonMessage).extractingJsonPathNumberValue("@.receiver.id").isEqualTo(2);

        assertThat(jsonMessage).hasJsonPath("@.receiver.username");
        assertThat(jsonMessage).extractingJsonPathStringValue("@.receiver.username").isEqualTo("tanya");


    }

    @Test
    void messageDeserializationTest() throws IOException {
        String content = """
            {
                "id": 1,
                "content": "Привет!",
                "owner": {
                    "id": 1,
                    "username": "nikita",
                    "email": "nikita@example.com"
                },
                "receiver": {
                    "id": 2,
                    "username": "tanya",
                    "email": "tanya@example.com"
                },
                "createdAt": "2025-03-19T10:30:00"
            }
            """;

        User nikita = new User(1L, "nikita", "nikita@example.com", null);
        User tanya = new User(2L, "tanya", "tanya@example.com", null);
        Message expected = new Message(1L, "Привет!", nikita, tanya);

        assertThat(json.parse(content)).isEqualTo(expected);
        assertThat(json.parseObject(content).getId()).isEqualTo(1L);
        assertThat(json.parseObject(content).getContent()).isEqualTo("Привет!");
        assertThat(json.parseObject(content).getOwner().getUsername()).isEqualTo("nikita");
        assertThat(json.parseObject(content).getReceiver().getUsername()).isEqualTo("tanya");
    }

//    @Test
//    void messageListSerializationTest() throws IOException {
//        assertThat(jsonList.write(messages)).isStrictlyEqualToJson("list.json");
//    }
//
//    @Test
//    void messageListDeserializationTest() throws IOException {
//        String expected = """
//            [
//                {
//                    "id": 1,
//                    "content": "Привет!",
//                    "user": {"id": 1, "username": "nikita", "email": "nikita@example.com", "password": "123"},
//                    "createdAt": "2025-03-19T10:30:00"
//                },
//                {
//                    "id": 2,
//                    "content": "Привет, как дела?",
//                    "user": {"id": 2, "username": "tanya", "email": "tanya@example.com", "password": "qwerty"},
//                    "createdAt": "2025-03-19T10:31:00"
//                },
//                {
//                    "id": 3,
//                    "content": "Отлично!",
//                    "user": {"id": 1, "username": "nikita", "email": "nikita@example.com", "password": "123"},
//                    "createdAt": "2025-03-19T10:32:00"
//                }
//            ]
//            """;
//        assertThat(jsonList.parse(expected)).isEqualTo(messages);
//    }
}

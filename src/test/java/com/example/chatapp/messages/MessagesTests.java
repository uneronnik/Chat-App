package com.example.chatapp.messages;

import com.example.chatapp.users.User;
import com.example.chatapp.users.UserRepository;
import com.example.chatapp.users.UserService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public class MessagesTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User receiver;
    private User receiver2;
    private User sender;
    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
        userService.register("nikita", "nikita@example.com", "b123");
        receiver = userService.register("tanya", "tanya@example.com", "qwerty");
        receiver2 = userService.register("alya", "alya@example.com", "zxc");
        sender = userService.register("test", "test@example.com", "b123");


    }
    @Test
    @DirtiesContext
    void shouldSendMessage() {

        MessageRequest newMessage = new MessageRequest("Hello World!!!");

        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> getResponseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .getForEntity("/messages/" + receiver.getUsername(), String.class);

        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponseEntity.getBody());

        List<String> messages = documentContext.read("$.[*].content");
        assertThat(messages.get(0)).isEqualTo("Hello World!!!");
    }

    @Test
    @DirtiesContext
    void shouldNotSendMessageIfIncorrectUserData() {

        MessageRequest newMessage = new MessageRequest("Test message!");

        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test123123", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        responseEntity = restTemplate
                .withBasicAuth("test", "b1233213123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    @DirtiesContext
    void shouldNotSendMessageToNotExistingUser() {
        MessageRequest newMessage = new MessageRequest("Test message!");

        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/99999999999999", newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    @DirtiesContext
    void shouldNotSendEmptyMessage() {

        MessageRequest newMessage = new MessageRequest("");

        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    @DirtiesContext
    void shouldNotSendMessageWithoutAuth() {
        MessageRequest newMessage = new MessageRequest("Test message!");

        ResponseEntity<Void> responseEntity = restTemplate
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void shouldNotSendMessageToSelf() {

        MessageRequest newMessage = new MessageRequest("Test message!");

        Optional<User> sender = userRepository.findByUsername("test");

        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + sender.get().getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    void shouldReturnNotFoundWhenNoMessages() {
        ResponseEntity<String> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .getForEntity("/messages/" + 2L, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void shouldGetAllMessagesWithUser() {

        MessageRequest newMessage = new MessageRequest("Hello World1!!!");
        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        newMessage = new MessageRequest("Hello World2!!!");
        responseEntity = restTemplate
                .withBasicAuth("tanya", "qwerty")
                .postForEntity("/messages/" + sender.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        newMessage = new MessageRequest("Hello World3!!!");
        responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> getResponseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .getForEntity("/messages/" + receiver.getUsername(), String.class);

        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponseEntity.getBody());

        List<Object> messages = documentContext.read("$[*]");
        assertThat(messages.size()).isEqualTo(3);

        List<String> contents = documentContext.read("$..content");

        assertThat(contents.get(0)).isEqualTo("Hello World1!!!");
        assertThat(contents.get(1)).isEqualTo("Hello World2!!!");
        assertThat(contents.get(2)).isEqualTo("Hello World3!!!");
    }
    @Test
    void shouldNotGetMessagesWithoutAuth() {

        ResponseEntity<String> getResponseEntity = restTemplate
                .getForEntity("/messages/" + receiver.getId(), String.class);

        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    void shouldNotReturnMessagesFromOtherChats() {
        MessageRequest newMessage = new MessageRequest("Hello World1!!!");
        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        newMessage = new MessageRequest("Hello World2!!!");
        responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> getResponseEntity = restTemplate
                .withBasicAuth("nikita", "b123")
                .getForEntity("/messages/" + receiver.getUsername(), String.class);

        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }
    @Test
    void shouldNotGetMessagesFromNonExistentUser() {
        ResponseEntity<String> getResponseEntity = restTemplate
                .withBasicAuth("nikita", "b123")
                .getForEntity("/messages/asdasdasdasd", String.class);

        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnAllUsersWithExistingConversations() {
        MessageRequest newMessage = new MessageRequest("Hello World1!!!");
        ResponseEntity<Void> responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        newMessage = new MessageRequest("Hello World2!!!");
        responseEntity = restTemplate
                .withBasicAuth("tanya", "qwerty")
                .postForEntity("/messages/" + sender.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        newMessage = new MessageRequest("Hello World3!!!");
        responseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .postForEntity("/messages/" + receiver2.getUsername(), newMessage, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);



        ResponseEntity<String> getResponseEntity = restTemplate
                .withBasicAuth("test", "b123")
                .getForEntity("/messages", String.class);
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponseEntity.getBody());

        List<Object> users = documentContext.read("$[*]");
        assertThat(users.size()).isEqualTo(2);

        List<String> names = documentContext.read("$..username");

        assertThat(names).containsExactlyInAnyOrder("tanya", "alya");
    }
}

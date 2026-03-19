package com.example.chatapp.users;

import com.example.chatapp.ErrorResponse;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public class AuthTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userService.register("nikita", "nikita@example.com", "b123");
        userService.register("tanya", "tanya@example.com", "qwerty");
        userService.register("alya", "alya@example.com", "zxc");
        userService.register("test", "test@example.com", "b123");

    }

    /*@Test
    public void dataLoaded(){
        User user = new User(null, "nikita", "nikita@example.com", "123");

        Optional<User> optionalUserFromRep = userRepository.findByUsername(user.getUsername());
        assertThat(optionalUserFromRep.isPresent()).isEqualTo(true);

        User createdUser = optionalUserFromRep.get();

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
    }*/
    @Test
    @DirtiesContext
    void shouldCreateANewUser() {
        RegisterRequest user = new RegisterRequest( "oleg", "oleg@example.com", "btest");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/auth/register", user, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<User> optionalUserFromRep = userRepository.findByUsername(user.username());
        assertThat(optionalUserFromRep.isPresent()).isEqualTo(true);

        User createdUser = optionalUserFromRep.get();

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(user.username());
        assertThat(createdUser.getEmail()).isEqualTo(user.email());
        assertThat(createdUser.getPassword()).startsWith("$2a$"); // пароль захеширован
    }

    @Test
    void shouldNotCreateUserWithInvalidData() {
        //Неправильный username
        RegisterRequest registerRequest = new RegisterRequest("", "r@example.com", "b123");
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.postForEntity("/auth/register", registerRequest, ErrorResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        //Неправильная почта
        registerRequest = new RegisterRequest("nikita", "r.com", "b123");
        responseEntity = restTemplate.postForEntity("/auth/register", registerRequest, ErrorResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        //Неправильный пароль
        registerRequest = new RegisterRequest("nikita", "r@example.com", "");
        responseEntity = restTemplate.postForEntity("/auth/register", registerRequest, ErrorResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    void shouldNotCreateUserWithDuplicateUsername() {
        RegisterRequest registerRequest = new RegisterRequest("nikita", "r@example.com", "b123");

        ResponseEntity<ErrorResponse> responseEntity = restTemplate.postForEntity("/auth/register", registerRequest, ErrorResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        assertThat(responseEntity.getBody().code()).isEqualTo("USERNAME_EXISTS");
    }
    @Test
    @DirtiesContext
    void shouldNotCreateUserWithDuplicateEmail() {
        RegisterRequest newUser = new RegisterRequest( "qwerty", "nikita@example.com", "b123");

        ResponseEntity<ErrorResponse> responseEntity = restTemplate.postForEntity("/auth/register", newUser, ErrorResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        assertThat(responseEntity.getBody().code()).isEqualTo("EMAIL_EXISTS");
    }


}

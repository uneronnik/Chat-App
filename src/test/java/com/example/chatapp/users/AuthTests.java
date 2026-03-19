package com.example.chatapp.users;

import com.example.chatapp.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class AuthTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;
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
        RegisterRequest user = new RegisterRequest( "oleg", "oleg@example.com", "test");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/register", user, String.class);
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

    }

    @Test
    @DirtiesContext
    void shouldNotCreateUserWithDuplicateUsername() {
        RegisterRequest registerRequest = new RegisterRequest("nikita", "r@example.com", "123");

        ResponseEntity<ErrorResponse> responseEntity = restTemplate.postForEntity("/register", registerRequest, ErrorResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        assertThat(responseEntity.getBody().code()).isEqualTo("USERNAME_EXISTS");
    }
    @Test
    @DirtiesContext
    void shouldNotCreateUserWithDuplicateEmail() {
        RegisterRequest newUser = new RegisterRequest( "qwerty", "nikita@example.com", "123");

        ResponseEntity<ErrorResponse> responseEntity = restTemplate.postForEntity("/register", newUser, ErrorResponse.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        assertThat(responseEntity.getBody().code()).isEqualTo("EMAIL_EXISTS");
    }


}

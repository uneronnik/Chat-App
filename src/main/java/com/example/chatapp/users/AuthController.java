package com.example.chatapp.users;

import com.example.chatapp.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping
@Tag(name = "Auth", description = "регистрация и авторизация")
public class AuthController {
    private final UserService userService;
    private final  UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь уже существует",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "USERNAME_EXISTS",
                                            summary = "Имя занято",
                                            value = """
                                            {"code": "USERNAME_EXISTS", "message": "Пользователь именем nikita уже существует"}
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "EMAIL_EXISTS",
                                            summary = "Почта занята",
                                            value = """
                                            {"code": "EMAIL_EXISTS", "message": "Пользователь с почтой 'nikita@example.com' уже существует"}
                                            """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ErrorResponse> registerNewUser(@RequestBody RegisterRequest newUser, Principal principal) {
        System.out.println("DEBUG requestPassword: " + newUser.password());
        //Ошибка если пользователь с таким именем уже есть
        if(userRepository.findByUsername(newUser.username()).isPresent()) {
            String msg = """
            Пользователь именем %s уже существует
            """.formatted(newUser.username());

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("USERNAME_EXISTS", msg));
        }
        //Ошибка если пользователь с такой почтой уже есть
        if(userRepository.findByEmail(newUser.email()).isPresent()) {
            String msg = """
            Пользователь c почтой %s уже существует
            """.formatted(newUser.email());

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("EMAIL_EXISTS", msg));
        }

        userService.register(newUser.username(), newUser.email(), newUser.password());
        return ResponseEntity.ok().build();
    }
}

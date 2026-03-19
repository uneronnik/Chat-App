package com.example.chatapp.messages;

import com.example.chatapp.users.User;
import com.example.chatapp.users.UserRepository;
import com.example.chatapp.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
@Tag(name = "Message", description = "отправка и чтение сообщений")
public class MessageController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserService userService;

    private MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Operation(summary = "Отправить сообщение")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Сообщение успешно отправлено"),
            @ApiResponse(responseCode = "400",
                    description = "Сообщение пустое или пользователь пытается отправить сообщение себе"),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404",
                    description = "Получатель не найден")
    })
    @PostMapping("/{receiverUsername}")
    public ResponseEntity<Void> sendMessage(@PathVariable String receiverUsername, @Valid @RequestBody MessageRequest message, Principal principal) {

        Optional<User> sender = userRepository.findByUsername(principal.getName());
        Optional<User> optionalReceiver = userRepository.findByUsername(receiverUsername);
        if(optionalReceiver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User receiver = optionalReceiver.get();
        if(receiver.getId().equals(sender.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Message msg = new Message(null, message.content(), sender.get(), receiver);

        messageRepository.save(msg);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
    @Operation(summary = "Получить все сообщения в чате с пользователем")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Список сообщений в чате",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))),
            @ApiResponse(responseCode = "404", description = "Сообщения не найдены", content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content)
    })
    @GetMapping("/{otherUsername}")
    public ResponseEntity<Iterable<Message>> getMessagesWithUser(@PathVariable String otherUsername, Principal principal) {
        List<Message> allMessages = messageRepository.findChatMessages(principal.getName(), otherUsername);
        if(allMessages.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allMessages);
    }
    @Operation(summary = "Получить всех пользователей с которыми у пользователя есть сообщения")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Список пользователей",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "404",
                    description = "Пользователи не найдены",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content)
    })
    @GetMapping()
    public ResponseEntity<Iterable<User>> AllUsersWithExistingConversations(Principal principal) {
        List<User> allUsersWithConversations = messageRepository.findAllConversationPartners(principal.getName());

        if(allUsersWithConversations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allUsersWithConversations);
    }
}

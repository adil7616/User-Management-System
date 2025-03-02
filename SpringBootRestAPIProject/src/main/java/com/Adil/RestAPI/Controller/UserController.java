package com.Adil.RestAPI.Controller;

import com.Adil.RestAPI.model.*;
import com.Adil.RestAPI.model.User;
import com.Adil.RestAPI.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.ok(Map.of(
                    "message", "User created successfully",
                    "user_id", user.getUserId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    @PostMapping("/get_users")
    public ResponseEntity<?> getUsers(@RequestBody GetUsersRequest request) {
        List<User> users = userService.getUsers(request);
        List<Map<String, Object>> response = users.stream().map(this::userToMap).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("users", response));
    }

    @PostMapping("/delete_user")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserRequest request) {
        try {
            userService.deleteUser(request);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    @PostMapping("/update_user")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request) {
        try {
            userService.updateUser(request);
            return ResponseEntity.ok(Map.of("message", "User(s) updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", user.getUserId());
        map.put("manager_id", user.getManager() != null ? user.getManager().getManagerId() : null);
        map.put("full_name", user.getFullName());
        map.put("mob_num", user.getMobNum());
        map.put("pan_num", user.getPanNum());
        map.put("created_at", user.getCreatedAt());
        map.put("updated_at", user.getUpdatedAt());
        map.put("is_active", user.isActive());
        return map;
    }

    private Map<String, String> errorResponse(String message) {
        return Map.of("error", message);
    }
}
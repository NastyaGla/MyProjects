package com.example.demo1.controllers;
import com.example.demo1.config.auth.TokenProvider;
import com.example.demo1.dtos.JwtDto;
import com.example.demo1.dtos.UserDto;
import com.example.demo1.entities.User;
import com.example.demo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenProvider tokenService;

    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> requestBody, @AuthenticationPrincipal UserDetails currentUser) {
        String login = requestBody.get("login");
        userService.deleteUser(login, currentUser);
        return ResponseEntity.ok("User deleted successfully");
    }
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        List<UserDto> userDto = userService.getAllUsers();
        if (userDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userDto);
    }
    @PostMapping("/changeLogin")
    public ResponseEntity<JwtDto> changeLogin(@RequestBody Map<String, String> requestBody, @AuthenticationPrincipal UserDetails currentUser){
        String newLogin = requestBody.get("login");
        User updatedUser  = userService.changeLogin(newLogin, currentUser);
        String newToken = tokenService.generateAccessToken(updatedUser);
        return ResponseEntity.ok(new JwtDto(newToken));
    }
    @PostMapping("/changePassword")
    public ResponseEntity<JwtDto> changePassword(@RequestBody Map<String, String> requestBody, @AuthenticationPrincipal UserDetails currentUser){
        String newPassword = requestBody.get("password");
        User updatedUser  = userService.changePassword(newPassword, currentUser);
        String newToken = tokenService.generateAccessToken(updatedUser);
        return ResponseEntity.ok(new JwtDto(newToken));
    }
}

package com.example.demo1.service;

import com.example.demo1.dtos.UserDto;
import com.example.demo1.entities.User;
import com.example.demo1.repositories.UserRepository;
import exeptions.InvalidJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void deleteUser(String login, UserDetails currentUser) {
        UserDetails user = userRepository.findByLogin(login);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (currentUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
            if (!currentUser.getUsername().equals(login)) {
                throw new AccessDeniedException("You can only delete your own account");
            }
        }

        userRepository.delete(user);
    }
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDto(user.getUsername(), user.getRole().name()))
                .collect(Collectors.toList());
    }
    public User changeLogin(String newLogin, UserDetails currentUser) {
        User user = userRepository.findByLogin(currentUser.getUsername());

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (userRepository.findByLogin(newLogin) != null) {
            throw new InvalidJwtException("Username already exists");
        }

        user.setLogin(newLogin);
        userRepository.save(user);

        return user;
    }
    public User changePassword(String newPassword, UserDetails currentUser) {
        User user = userRepository.findByLogin(currentUser.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        return user;
    }
}

package com.example.demo1.repositories;

import com.example.demo1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    void delete(UserDetails user);
}
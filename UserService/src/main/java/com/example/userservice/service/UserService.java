package com.example.userservice.service;

import com.example.userservice.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User updateUser(int id, User userDetails);
    Optional<User> getUserById(int id);
    List<User> getAllUsers();
    Optional<User> getUserByEmail(String email);
    void deleteUser(int id);
}


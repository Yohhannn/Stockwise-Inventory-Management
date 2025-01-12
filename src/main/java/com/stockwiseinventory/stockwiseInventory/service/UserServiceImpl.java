package com.stockwiseinventory.stockwiseInventory.service;

import com.stockwiseinventory.stockwiseInventory.model.User;
import com.stockwiseinventory.stockwiseInventory.model.UserDTO;
import com.stockwiseinventory.stockwiseInventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // Save (Create or Update) an account
    @Override
    public User saveAccount(User account) {
        return userRepository.save(account);
    }

    // Login logic
    @Override
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        if ("admin@gmail.com".equals(email) && "admin123".equals(password)) {
            String token = generateToken(email); // Call generateToken
            response.put("status", "success");
            response.put("token", token);
            response.put("userId", "admin123"); // Just for example
            return response;
        }

        Optional<User> user = userRepository.findByEmailAndPassword(email, password);

        if (user.isPresent()) {
            String token = generateToken(user.get().getEmail()); // Call generateToken
            response.put("status", "success");
            response.put("token", token);
            response.put("userId", user.get().getId()); // User ID from the database
        } else {
            response.put("status", "error");
            response.put("message", "Invalid login credentials");
        }

        return response;
    }

    private String generateToken(String email) {
        return email;
    }


    // Retrieve all new users this month
    @Override
    public List<User> getNewUsersThisMonth() {
        return userRepository.findUsersRegisteredThisMonth();
    }

    // Count of new users registered this month
    @Override
    public long getNewUsersCountThisMonth() {
        return userRepository.findUsersRegisteredThisMonth().size();
    }

    // Count of active users
    @Override
    public long getActiveUsersCount() {
        return userRepository.countActiveUsers();
    }

    // Get all users
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Create a new account (wrap saveAccount for clarity if needed)
    @Override
    public User createUser(User user) {
        return saveAccount(user);
    }

    // Delete a user by ID
    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    // Update account details
    @Override
    public User updateUser(int userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        existingUser.setFirst_name(updatedUser.getFirst_name());
        existingUser.setLast_name(updatedUser.getLast_name());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setStatus(updatedUser.getStatus());

        return userRepository.save(existingUser);
    }

    //WALA NIY PASSWORD HAHAHA
    @Override
    public List<UserDTO> getAllUsersDefined() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getFirst_name(),
                        user.getLast_name(),
                        user.getEmail(),
                        user.getStatus(),
                        user.getCreatedAt()))
                .collect(Collectors.toList());
    }


}
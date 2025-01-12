package com.stockwiseinventory.stockwiseInventory.service;

import com.stockwiseinventory.stockwiseInventory.model.User;
import com.stockwiseinventory.stockwiseInventory.model.UserDTO;

import java.util.List;
import java.util.Map;

public interface UserService {
    User saveAccount(User account);
    List<User> getNewUsersThisMonth();
    long getNewUsersCountThisMonth();
    long getActiveUsersCount();
    List<User> getAllUsers();
    User createUser(User user);
    Map<String, Object> login(String email, String password);
    void deleteUser(int userId);
    User updateUser(int userId, User updatedUser);
    List<UserDTO> getAllUsersDefined();
}

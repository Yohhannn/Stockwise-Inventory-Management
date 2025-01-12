package com.stockwiseinventory.stockwiseInventory.controller;

import com.stockwiseinventory.stockwiseInventory.model.User;
import com.stockwiseinventory.stockwiseInventory.repository.UserRepository;
import com.stockwiseinventory.stockwiseInventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/register")
@CrossOrigin
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody User user) {
        // Check if the username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username is already taken.");
        }

        // If username doesn't exist, save the account
        userService.saveAccount(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Account Added.");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        System.out.println("Received Email: " + email);
        System.out.println("Received Password: " + password);

        Map<String, Object> response = userService.login(email, password);

        // Check the response and send back a proper HTTP status code
        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response); // Return successful response with token
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Unauthorized if login failed
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean usernameExists = userRepository.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("usernameExists", usernameExists);
        return ResponseEntity.ok(response);
    }
}



//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/register")
//public class LoginController {
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/add")
//    public String add(@RequestBody User user){
//        userService.saveAccount(user);
//        return "New Account Added.";
//    }
//
//    @PostMapping("/authenticate")
//    public String login(@RequestBody Map<String, String> credentials) {
//        String username = credentials.get("username");
//        String password = credentials.get("password");
//        return userService.login(username, password);
//    }
//}

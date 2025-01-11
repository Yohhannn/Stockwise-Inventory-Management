package com.stockwiseinventory.stockwiseInventory.controller;

import com.stockwiseinventory.stockwiseInventory.model.Cart;
import com.stockwiseinventory.stockwiseInventory.model.CartRequest;
import com.stockwiseinventory.stockwiseInventory.model.Order;
import com.stockwiseinventory.stockwiseInventory.model.User;
import com.stockwiseinventory.stockwiseInventory.service.CartService;
import com.stockwiseinventory.stockwiseInventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static javax.crypto.Cipher.SECRET_KEY;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // your React app's URL
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    private static final String SECRET_KEY = "YzMzQ2I3YjEwN2FiODk1ZmRhNzk4M2Ey"; // Example secure key

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // Use email as the subject
                .claim("userId", user.getId()) // Include userId in claims
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiry
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }


    // Add a product to the user's cart
    @PostMapping
    public ResponseEntity<Void> addToCart(@RequestBody CartRequest cartRequest) {
        cartService.addToCart(cartRequest.getUserId(), cartRequest.getProductId(), cartRequest.getQuantity());
        return ResponseEntity.ok().build();
    }


    // Get all cart items for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Cart>> getAllCartItems(@PathVariable int userId) {
        System.out.println("UserID received: " + userId);
        List<Cart> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    // Update the quantity of an existing cart item
    @PutMapping("/{cartId}")
    public ResponseEntity<Cart> updateCartQuantity(
            @PathVariable int cartId,
            @RequestBody Map<String, Integer> requestBody) {

        int quantity = requestBody.get("quantity"); // Assuming body has a "quantity" field

        Cart updatedCart = cartService.updateQuantity(cartId, quantity);

        return ResponseEntity.ok(updatedCart);
    }

    // Get a specific cart item by cartId
    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartItemById(@PathVariable int cartId) {
        return cartService.getCartById(cartId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Remove a product from the cart by cartId
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable int cartId) {
        cartService.removeFromCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestParam int userId, @RequestBody List<Integer> cartItemIds) {
        // Call service to process the order
        Order order = orderService.checkout(userId, cartItemIds);  // Now this method returns Order

        // Prepare the response containing order_id
        Map<String, Object> response = new HashMap<>();
        response.put("order_id", order.getOrderId());  // Getting the orderId from the returned Order object

        // Log the response to ensure the correct format
        System.out.println("Response being sent: " + response);

        return ResponseEntity.ok(response);  // Sending order ID in the response
    }



}

//package com.stockwiseinventory.stockwiseInventory.controller;
//
//import com.stockwiseinventory.stockwiseInventory.model.Cart;
//import com.stockwiseinventory.stockwiseInventory.model.CartRequest;
//import com.stockwiseinventory.stockwiseInventory.model.Order;
//import com.stockwiseinventory.stockwiseInventory.service.CartService;
//import com.stockwiseinventory.stockwiseInventory.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/carts")
//@CrossOrigin
//public class CartController {
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private OrderService orderService;
//
//    // Add a product to the user's cart
//    @PostMapping
//    public ResponseEntity<Void> addToCart(@RequestBody CartRequest cartRequest) {
//        cartService.addToCart(cartRequest.getUserId(), cartRequest.getProductId(), cartRequest.getQuantity());
//        return ResponseEntity.ok().build();
//    }
//
//
//    // Get all cart items for a specific user
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Cart>> getAllCartItems(@PathVariable int userId) {
//        List<Cart> cartItems = cartService.getCartItems(userId);
//        return ResponseEntity.ok(cartItems);
//    }
//
//    // Update the quantity of an existing cart item
//    @PutMapping("/{cartId}")
//    public ResponseEntity<Cart> updateCartQuantity(
//            @PathVariable int cartId,
//            @RequestBody Map<String, Integer> requestBody) {
//
//        int quantity = requestBody.get("quantity"); // Assuming body has a "quantity" field
//
//        Cart updatedCart = cartService.updateQuantity(cartId, quantity);
//
//        return ResponseEntity.ok(updatedCart);
//    }
//
//    // Get a specific item by cartId
//    @GetMapping("/{cartId}")
//    public ResponseEntity<Cart> getCartItemById(@PathVariable int cartId) {
//        return cartService.getCartById(cartId)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // Remove a product from the cart by cartId
//    @DeleteMapping("/{cartId}")
//    public ResponseEntity<Void> removeProductFromCart(@PathVariable int cartId) {
//        cartService.removeFromCart(cartId);
//        return ResponseEntity.noContent().build();
//    }
//
//    //checkout function in the Cart Controller
//    @PostMapping("/checkout")
//    public ResponseEntity<Map<String, Object>> checkout(@RequestParam int userId, @RequestBody List<Integer> cartItemIds) {
//        // Call service to process the order
//        Order order = orderService.checkout(userId, cartItemIds);  // Now this method returns Order
//
//        // Prepare the response containing order_id
//        Map<String, Object> response = new HashMap<>();
//        response.put("order_id", order.getOrderId());  // Getting the orderId from the returned Order object
//
//        // Log the response to ensure the correct format
//        System.out.println("Response being sent: " + response);
//
//        return ResponseEntity.ok(response);  // Sending order ID in the response
//    }
//}

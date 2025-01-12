package com.stockwiseinventory.stockwiseInventory.controller;

import com.stockwiseinventory.stockwiseInventory.model.OrderRequest;
import com.stockwiseinventory.stockwiseInventory.model.OrderResponse;
import com.stockwiseinventory.stockwiseInventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@CrossOrigin(origins = "http://localhost:3000") // your React app's URL
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/finalize")
    public ResponseEntity<Map<String, Object>> finalizeOrder(@RequestBody OrderRequest request) {
        try {
            String companyName = Optional.ofNullable(request.getCompanyName()).orElse("N/A");
            String phone = Optional.ofNullable(request.getPhone()).orElse("N/A");

            String state = request.getOptionalState().orElse("N/A");
            String zipCode = request.getOptionalZipCode().orElse("N/A");

            orderService.finalizeOrder(
                    request.getOrderId(),
                    request.getFirstName(),
                    request.getLastName(),
                    companyName,
                    phone,
                    request.getStreetAddress(),
                    request.getCity(),
                    state,
                    zipCode
            );

            // Return success as JSON
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order finalized successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Return error message as JSON
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable int id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

}
//package com.stockwiseinventory.stockwiseInventory.controller;
//
//import com.stockwiseinventory.stockwiseInventory.model.OrderRequest;
//import com.stockwiseinventory.stockwiseInventory.model.OrderResponse;
//import com.stockwiseinventory.stockwiseInventory.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//
//
//@RestController
//@CrossOrigin(origins = "http://localhost:3000") // your React app's URL
//@RequestMapping("/api/orders")
//public class OrderController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @PostMapping("/finalize")
//    public ResponseEntity<Map<String, Object>> finalizeOrder(@RequestBody OrderRequest request) {
//        try {
//            String companyName = Optional.ofNullable(request.getCompanyName()).orElse("N/A");
//            String phone = Optional.ofNullable(request.getPhone()).orElse("N/A");
//
//            String state = request.getOptionalState().orElse("N/A");
//            String zipCode = request.getOptionalZipCode().orElse("N/A");
//
//            orderService.finalizeOrder(
//                    request.getOrderId(),
//                    request.getFirstName(),
//                    request.getLastName(),
//                    companyName,
//                    phone,
//                    request.getStreetAddress(),
//                    request.getCity(),
//                    state,
//                    zipCode
//            );
//
//            // Return success as JSON
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "Order finalized successfully");
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            // Return error message as JSON
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<OrderResponse> getOrderById(@PathVariable int id) {
//        OrderResponse response = orderService.getOrderById(id);
//        return ResponseEntity.ok(response);
//    }
//
//}
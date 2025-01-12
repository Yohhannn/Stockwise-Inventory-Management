package com.stockwiseinventory.stockwiseInventory.service;

import com.stockwiseinventory.stockwiseInventory.model.Order;
import com.stockwiseinventory.stockwiseInventory.model.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order checkout(int userId, List<Integer> cartItemIds);

    void finalizeOrder(int orderId, String firstName, String lastName, String companyName, String phone,
                       String streetAddress, String city, String state, String zipCode);

    void updateOrderDeliveryStatus();

    OrderResponse getOrderById(int orderId);


}
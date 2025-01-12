package com.stockwiseinventory.stockwiseInventory.service;

import com.stockwiseinventory.stockwiseInventory.model.*;
import com.stockwiseinventory.stockwiseInventory.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Transactional
    @Override
    public Order checkout(int userId, List<Integer> cartItemIds) {
        List<Cart> selectedItems = cartRepository.findAllById(cartItemIds);

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("No items selected for checkout");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Creating a temporary order for the user
        Order temporaryOrder = new Order();
        temporaryOrder.setUser(user);
        temporaryOrder.setOrderStatus("Pending");
        temporaryOrder.setCheckoutDate(LocalDateTime.now());

        orderRepository.save(temporaryOrder);  // Saving the order

        for (Cart item : selectedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(temporaryOrder);
            orderItem.setProductId(item.getProduct().getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());

            orderItemRepository.save(orderItem);  // Save associated items
        }

        // Optionally delete the items from the cart after checkout
        // REMEMBER TO DELETE THIS KAY WALA NAMAN NA SA CART
    cartRepository.deleteAll(selectedItems);

        // Log the temporary order ID and user ID for debugging
        System.out.println("Order with ID " + temporaryOrder.getOrderId() + " created for user: " + user.getId());

        return temporaryOrder;  // Returning the entire Order object so that order ID can be retrieved
    }



    @Transactional
    @Override
    public void finalizeOrder(int orderId, String firstName, String lastName, String companyName, String phone,
                              String streetAddress, String city, String state, String zipCode) {
        System.out.println("Finalizing order with ID: " + orderId); // Log the orderId

        Order temporaryOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    System.out.println("Order with ID " + orderId + " not found.");
                    return new RuntimeException("Order not found");
                });

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setOrder(temporaryOrder);
        deliveryInfo.setFirstName(firstName);
        deliveryInfo.setLastName(lastName);
        deliveryInfo.setCompanyName(Optional.ofNullable(companyName).orElse("N/A"));
        deliveryInfo.setPhone(Optional.ofNullable(phone).orElse("N/A"));
        deliveryInfo.setStreetAddress(Optional.ofNullable(streetAddress).orElse("N/A"));
        deliveryInfo.setCity(city);
        deliveryInfo.setState(state);
        deliveryInfo.setZipCode(zipCode);

        deliveryInfoRepository.save(deliveryInfo);

        BigDecimal totalPrice = temporaryOrder.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = temporaryOrder.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // Set delivery date (15 days from now)
        LocalDateTime deliveryDate = LocalDateTime.now().plusDays(15);
        temporaryOrder.setDeliveryDate(deliveryDate);

        temporaryOrder.setTotalPrice(totalPrice);
        temporaryOrder.setTotalItems(totalItems);
        temporaryOrder.setOrderStatus("Confirmed");

        orderRepository.save(temporaryOrder);
    }

    @Override
    @Scheduled(fixedRate = 86400000) // Every 24 hours
    @Transactional
    public void updateOrderDeliveryStatus() {
        LocalDateTime now = LocalDateTime.now();

        List<Order> ordersToDeliver = orderRepository.findAllByOrderStatusAndDeliveryDateBefore("Confirmed", now);

        for (Order order : ordersToDeliver) {
            order.setOrderStatus("Delivered");
            orderRepository.save(order);
        }
    }

    @Override
    public OrderResponse getOrderById(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return new OrderResponse(order);
    }

}

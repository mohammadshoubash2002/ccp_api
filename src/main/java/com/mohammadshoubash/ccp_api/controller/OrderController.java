package com.mohammadshoubash.ccp_api.controller;

import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mohammadshoubash.ccp_api.dto.MessageResponse;
import com.mohammadshoubash.ccp_api.dto.OrderRequest;
import com.mohammadshoubash.ccp_api.dto.OrderResponse;
import com.mohammadshoubash.ccp_api.dto.PageResponse;
import com.mohammadshoubash.ccp_api.entity.OrderStatus;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        return orderService.getOrderById(id, username);
    }

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatus status, Principal principal) {
        return orderService.updateOrderStatus(id, status, principal.getName());
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return new MessageResponse("Order deleted successfully");
    }

    @GetMapping("/search")
    public PageResponse<Order> getOrdersByFilters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Page<Order> orderPage = orderService.getOrdersByFilters(status, sort, page, pageSize);
        return PageResponse.from(orderPage);
    }

    @GetMapping("/my-orders")
    public List<Order> getMyOrders(Principal principal) {
        return orderService.getMyOrders(principal.getName());
    }
}

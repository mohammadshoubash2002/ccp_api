package com.mohammadshoubash.ccp_api.controller;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Order createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @PatchMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return new MessageResponse("Order deleted successfully");
    }

    @GetMapping("/search")
    public PageResponse<Order> getOrdersByFilters(@RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Page<Order> orderPage = orderService.getOrdersByFilters(status, sort, page, pageSize);
        return PageResponse.from(orderPage);
    }
}

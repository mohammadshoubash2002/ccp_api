package com.mohammadshoubash.ccp_api.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.entity.OrderStatus;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;

public class OrderSpecification {
    public static Specification<Order> buildSpecification(String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                try {
                    OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), orderStatus));
                } catch (IllegalArgumentException e) {
                    throw new ResourceNotFoundException("Invalid order status: " + status);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

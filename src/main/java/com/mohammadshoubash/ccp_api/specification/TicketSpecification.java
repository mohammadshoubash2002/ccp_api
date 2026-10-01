package com.mohammadshoubash.ccp_api.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import com.mohammadshoubash.ccp_api.entity.Ticket;
import com.mohammadshoubash.ccp_api.entity.TicketStatus;
import com.mohammadshoubash.ccp_api.entity.TicketPriority;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;

public class TicketSpecification {
    public static Specification<Ticket> buildSpecification(String status, String priority) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                try {
                    TicketStatus ticketStatus = TicketStatus.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), ticketStatus));
                } catch (IllegalArgumentException e) {
                    throw new ResourceNotFoundException("Invalid ticket status: " + status);
                }
            }

            if (priority != null) {
                try {
                    TicketPriority ticketPriority = TicketPriority.valueOf(priority.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("priority"), ticketPriority));
                } catch (IllegalArgumentException e) {
                    throw new ResourceNotFoundException("Invalid ticket priority: " + priority);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

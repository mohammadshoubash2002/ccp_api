package com.mohammadshoubash.ccp_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.mohammadshoubash.ccp_api.repository.TicketRepository;
import com.mohammadshoubash.ccp_api.specification.TicketSpecification;
import com.mohammadshoubash.ccp_api.repository.CustomerRepository;
import com.mohammadshoubash.ccp_api.entity.Ticket;
import com.mohammadshoubash.ccp_api.entity.TicketPriority;
import com.mohammadshoubash.ccp_api.entity.Customer;
import com.mohammadshoubash.ccp_api.entity.Order;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.entity.TicketStatus;
import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import com.mohammadshoubash.ccp_api.dto.OrderResponse;
import com.mohammadshoubash.ccp_api.dto.TicketRequest;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    @Autowired 
    private TicketRepository ticketRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Ticket createTicket(TicketRequest ticketRequest) {
        Ticket ticket = new Ticket();
        
        if (ticketRequest == null) {
            throw new IllegalArgumentException("Ticket and customer cannot be null");
        }
        
        Customer customer = customerRepository.findById(ticketRequest.customer_id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + ticketRequest.customer_id()));

        ticket.setCustomer(customer);
        ticket.setSubject(ticketRequest.subject());
        ticket.setStatus(TicketStatus.valueOf(ticketRequest.status().toUpperCase()));
        ticket.setPriority(TicketPriority.valueOf(ticketRequest.priority().toUpperCase()));

        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any order
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own order
        boolean isOwner = ticket.getCustomer() != null 
                && ticket.getCustomer().getUser() != null 
                && ticket.getCustomer().getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to view this ticket");
        }

        return ticket;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Ticket> getTicketsByCustomerId(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return ticketRepository.findByCustomerId(customer.get().getId());
        } else {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
    }

    public Ticket updateTicketStatus(Long id, TicketStatus status, String username) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);

        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any ticket
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own ticket
        boolean isOwner = ticketOpt.get().getCustomer() != null 
                && ticketOpt.get().getCustomer().getUser() != null 
                && ticketOpt.get().getCustomer().getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus(status);
            return ticketRepository.save(ticket);
        } else {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
    }

    public Ticket updateTicketPriority(Long id, TicketPriority priority, String username) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);

        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any ticket
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own ticket
        boolean isOwner = ticketOpt.get().getCustomer() != null 
                && ticketOpt.get().getCustomer().getUser() != null 
                && ticketOpt.get().getCustomer().getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setPriority(priority);
            return ticketRepository.save(ticket);
        } else {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Ticket> getTicketsByFilters(String status, String priority, String sort, Integer page, Integer pageSize) {
        Specification<Ticket> spec = TicketSpecification.buildSpecification(status, priority);

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            if (sort.startsWith("desc:")) {
                sortObj = Sort.by(Sort.Direction.DESC, sort.substring(5));
            } else if (sort.startsWith("asc:")) {
                sortObj = Sort.by(Sort.Direction.ASC, sort.substring(4));
            } else {
                sortObj = Sort.by(sort);
            }
        }
        
        int pageNumber = (page != null && page > 0) ? page - 1 : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        Pageable pageable = PageRequest.of(pageNumber, size, sortObj);

        return ticketRepository.findAll(spec, pageable);
    }

    public List<Ticket> getMyTickets(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getCustomer() == null) {
            throw new ResourceNotFoundException("No customer profile found for this user");
        }
        return ticketRepository.findByCustomerId(user.getCustomer().getId());
    }
}

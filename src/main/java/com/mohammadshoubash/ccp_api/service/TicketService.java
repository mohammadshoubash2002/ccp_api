package com.mohammadshoubash.ccp_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.mohammadshoubash.ccp_api.repository.TicketRepository;
import com.mohammadshoubash.ccp_api.specification.TicketSpecification;
import com.mohammadshoubash.ccp_api.entity.Ticket;
import com.mohammadshoubash.ccp_api.entity.TicketPriority;
import com.mohammadshoubash.ccp_api.entity.Role;
import com.mohammadshoubash.ccp_api.entity.TicketStatus;
import com.mohammadshoubash.ccp_api.entity.AppUser;
import com.mohammadshoubash.ccp_api.repository.AppUserRepository;
import com.mohammadshoubash.ccp_api.dto.TicketEventPayload;
import com.mohammadshoubash.ccp_api.dto.TicketRequest;
import com.mohammadshoubash.ccp_api.exception.ResourceNotFoundException;

import java.time.Instant;
import java.util.List;

@Service
public class TicketService {
    @Autowired 
    private TicketRepository ticketRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Ticket createTicket(TicketRequest ticketRequest) {
        if (ticketRequest == null) {
            throw new IllegalArgumentException("Ticket request cannot be null");
        }
        
        Ticket ticket = new Ticket();
        
        AppUser user = appUserRepository.findById(ticketRequest.customer_id())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + ticketRequest.customer_id()));

        ticket.setUser(user);
        ticket.setSubject(ticketRequest.subject());
        ticket.setStatus(TicketStatus.valueOf(ticketRequest.status().toUpperCase()));
        ticket.setPriority(TicketPriority.valueOf(ticketRequest.priority().toUpperCase()));

        Ticket savedTicket = ticketRepository.save(ticket);

        // Broadcast to WebSocket topic:
        messagingTemplate.convertAndSend("/topic/tickets", new TicketEventPayload(
            savedTicket.getId(),
            savedTicket.getUser() != null ? savedTicket.getUser().getId() : null,
            savedTicket.getStatus().name(),
            Instant.now().toString()
        ));

        return savedTicket;
    }

    public Ticket getTicketById(Long id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view any ticket
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only view their own ticket
        boolean isOwner = ticket.getUser() != null 
                && ticket.getUser().getUsername().equals(username);

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
        AppUser customer = appUserRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return ticketRepository.findByUserId(customer.getId());
    }

    public Ticket updateTicketStatus(Long id, TicketStatus status, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can view/update any ticket
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only update their own ticket
        boolean isOwner = ticket.getUser() != null 
                && ticket.getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        ticket.setStatus(status);
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Broadcast to WebSocket topic:
        messagingTemplate.convertAndSend("/topic/tickets", new TicketEventPayload(
            updatedTicket.getId(),
            updatedTicket.getUser() != null ? updatedTicket.getUser().getId() : null,
            updatedTicket.getStatus().name(),
            Instant.now().toString()
        ));
        
        return updatedTicket;
    }

    public Ticket updateTicketPriority(Long id, TicketPriority priority, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        AppUser currentUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Admin can update any ticket
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        // Customer can only update their own ticket
        boolean isOwner = ticket.getUser() != null 
                && ticket.getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        ticket.setPriority(priority);
        return ticketRepository.save(ticket);
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
        return ticketRepository.findByUserId(user.getId());
    }
}

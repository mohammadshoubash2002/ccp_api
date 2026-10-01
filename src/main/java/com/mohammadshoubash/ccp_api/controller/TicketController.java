package com.mohammadshoubash.ccp_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import jakarta.validation.Valid;

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
import com.mohammadshoubash.ccp_api.dto.PageResponse;
import com.mohammadshoubash.ccp_api.dto.TicketRequest;
import com.mohammadshoubash.ccp_api.entity.TicketPriority;
import com.mohammadshoubash.ccp_api.entity.TicketStatus;
import com.mohammadshoubash.ccp_api.entity.Ticket;
import com.mohammadshoubash.ccp_api.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    public Ticket createTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        return ticketService.createTicket(ticketRequest);
    }

    @PatchMapping("/{id}/status")
    public Ticket updateTicketStatus(@PathVariable Long id, @Valid @RequestBody TicketStatus status) {
        return ticketService.updateTicketStatus(id, status);
    }

    @PatchMapping("/{id}/priority")
    public Ticket updateTicketPriority(@PathVariable Long id, @Valid @RequestBody TicketPriority priority) {
        return ticketService.updateTicketPriority(id, priority);
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return new MessageResponse("Ticket deleted successfully");
    }

    @GetMapping("/search")
    public PageResponse<Ticket> getTicketsByFilters(@RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Page<Ticket> ticketPage = ticketService.getTicketsByFilters(status, priority, sort, page, pageSize);
        return PageResponse.from(ticketPage);
    }
}

package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.EventRequest;
import com.aueventmanagement.dto.TicketTypeRequest;
import com.aueventmanagement.dto.EventResponse;
import com.aueventmanagement.dto.TicketTypeResponse;
import com.aueventmanagement.entity.Event;
import com.aueventmanagement.entity.TicketType;
import com.aueventmanagement.entity.User;
import com.aueventmanagement.enums.EventStatus;
import com.aueventmanagement.enums.Role;
import com.aueventmanagement.repository.EventRepository;
import com.aueventmanagement.repository.TicketTypeRepository;
import com.aueventmanagement.repository.UserRepository;
import com.aueventmanagement.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor

public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        if(organizer.getRole() != Role.ORGANIZER){
            throw new RuntimeException("Only Organizer create event");
        }

        Event event = Event.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .venue(request.getVenue())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .salesStartDate(request.getSalesStartDate())
                        .salesEndDate(request.getSalesEndDate())
                        .status(EventStatus.PUBLISHED)
                        .organizer(organizer)
                        .build();


        List<TicketType> ticketTypes = new ArrayList<>();

        for (TicketTypeRequest dto : request.getTicketTypes()) {

            TicketType ticketType = TicketType.builder()
                    .name(dto.getTypeName())
                    .price(dto.getPrice())
                    .availableQuantity(dto.getAvailableQuantity())
                    .event(event)
                    .build();

            ticketTypes.add(ticketType);
        }

        event.setTicketTypes(ticketTypes);
        Event savedEvent = eventRepository.save(event);

        return mapToResponse(savedEvent);
    }

    @Override
    public EventResponse updateEvent(UUID eventId, EventRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));


        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event Not Found"));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException("You are not authorized to update this event.");
        }

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setVenue(request.getVenue());

        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());

        event.setSalesStartDate(request.getSalesStartDate());
        event.setSalesEndDate(request.getSalesEndDate());

        // Update/Create Ticket Types
        if (request.getTicketTypes() != null) {

            for (TicketTypeRequest dto : request.getTicketTypes()) {

                // Update existing ticket type
                if (dto.getId() != null) {

                    TicketType ticketType = ticketTypeRepository.findById(dto.getId())
                            .orElseThrow(() -> new RuntimeException("Ticket Type not found"));

                    ticketType.setName(dto.getTypeName());
                    ticketType.setPrice(dto.getPrice());
                    ticketType.setAvailableQuantity(dto.getAvailableQuantity());

                    ticketTypeRepository.save(ticketType);

                }
                // Create new ticket type
                else {

                    TicketType ticketType = TicketType.builder()
                            .name(dto.getTypeName())
                            .price(dto.getPrice())
                            .availableQuantity(dto.getAvailableQuantity())
                            .event(event)
                            .build();

                    ticketTypeRepository.save(ticketType);

                    event.getTicketTypes().add(ticketType);
                }
            }
        }

        Event updatedEvent = eventRepository.save(event);

        return mapToResponse(updatedEvent);
    }

    @Override
    public void deleteEvent(UUID eventId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Organizer not found")
                );

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found")
                );

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new RuntimeException(
                    "You are not authorized to delete this event"
            );
        }

        eventRepository.delete(event);
    }

    @Override
    public EventResponse getEventById(UUID eventId) {
       Event event =  eventRepository.findById(eventId)
               .orElseThrow(() -> new RuntimeException("Event not found"));
        return mapToResponse(event);
    }

    @Override
    public List<EventResponse> getMyEvents() {

        String email = SecurityContextHolder.getContext().
                getAuthentication()
                .getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        return eventRepository.findByOrganizer(organizer)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private EventResponse mapToResponse(Event event) {

        EventResponse response = new EventResponse();

        response.setId(event.getId());
        response.setName(event.getName());
        response.setDescription(event.getDescription());
        response.setVenue(event.getVenue());
        response.setStartDate(event.getStartDate());
        response.setEndDate(event.getEndDate());

        response.setSalesStartDate(event.getSalesStartDate());
        response.setSalesEndDate(event.getSalesEndDate());
        response.setStatus(event.getStatus());

        response.setOrganizerId(event.getOrganizer().getId());
        response.setOrganizerName(event.getOrganizer().getName());

        List<TicketTypeResponse> ticketResponses = event.getTicketTypes()
                .stream()
                .map(this::mapTicketResponse)
                .toList();

        response.setTicketTypes(ticketResponses);

        return response;
    }

    private TicketTypeResponse mapTicketResponse(TicketType ticketType) {

        TicketTypeResponse response = new TicketTypeResponse();

        response.setId(ticketType.getId());
        response.setTypeName(ticketType.getName());
        response.setPrice(ticketType.getPrice());
        response.setAvailableQuantity(ticketType.getAvailableQuantity());

        return response;
    }
}


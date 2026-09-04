package com.aueventmanagement.controller;

import com.aueventmanagement.dto.EventRequest;
import com.aueventmanagement.dto.EventResponse;
import com.aueventmanagement.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;

  @PostMapping
  @PreAuthorize("hasRole('ORGANIZER')")
  public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest
                                                             createEventRequest){
      return ResponseEntity.ok(eventService.createEvent(createEventRequest));
  }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable UUID eventId,
                                     @RequestBody EventRequest updateEventRequest){
        return ResponseEntity.ok(eventService.updateEvent(eventId, updateEventRequest));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<String> deleteEvent(@PathVariable UUID eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok("Event Deleted Successfully");
    }


    @GetMapping("/{eventId}")
//   @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse> getEventById(@PathVariable
                                          UUID eventId){
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/my-events")
//    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<EventResponse>> getMyEvents() {

        return ResponseEntity.ok(
                eventService.getMyEvents()
        );
    }


}

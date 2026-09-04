package com.aueventmanagement.service;

import com.aueventmanagement.dto.EventRequest;
import com.aueventmanagement.dto.EventResponse;

import java.util.List;
import java.util.UUID;

public interface EventService {
    EventResponse createEvent(EventRequest createEventRequest);

    EventResponse updateEvent(UUID eventId, EventRequest updateEventRequest);

    void deleteEvent(UUID eventId);
    EventResponse getEventById(UUID eventId);

    List<EventResponse> getMyEvents();

    List<EventResponse> getAllEvents();
}

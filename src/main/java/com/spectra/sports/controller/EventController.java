package com.spectra.sports.controller;

import com.spectra.sports.dto.EventDto;
import com.spectra.sports.entity.SportsEvent;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public SuccessResponse<EventDto> addEvent(@RequestBody SportsEvent event) {
        return eventService.addEvent(event);
    }

    @GetMapping("/{eventId}")
    public SuccessResponse<EventDto> getEventById(@PathVariable("eventId") Long eventId) {

        return eventService.getEventById(eventId);
    }

    @GetMapping("/all")
    public SuccessResponse<List<EventDto>> getAllEvents() {

        return eventService.getAllEventsByUserId();
    }

    @GetMapping("/ongoing")
    public SuccessResponse<List<EventDto>> getAllOngoingEvents() {

        return eventService.getAllOngoingEvents();
    }

    @DeleteMapping("/{eventId}")
    public SuccessResponse<Boolean> deleteEventById(@PathVariable("eventId") Long eventId) {

        return eventService.deleteEventById(eventId);
    }
}

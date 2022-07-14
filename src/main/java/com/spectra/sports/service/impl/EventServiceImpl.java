package com.spectra.sports.service.impl;

import com.spectra.sports.dto.EventDto;
import com.spectra.sports.entity.SportsEvent;
import com.spectra.sports.helper.UserContextHolder;
import com.spectra.sports.repository.EventRepository;
import com.spectra.sports.response.SuccessResponse;
import com.spectra.sports.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public SuccessResponse<EventDto> addEvent(SportsEvent sportsEvent) {
        Assert.notNull(sportsEvent, "Event cannot be null");
        var currentUser = UserContextHolder.getCurrentUser();
        sportsEvent.setCreatedBy(currentUser.userId());

        var newEvent = eventRepository.save(sportsEvent);

        return SuccessResponse.defaultResponse(EventDto.from(newEvent), "Event Added");
    }

    @Override
    public SuccessResponse<EventDto> getEventById(Long eventId) {
        var event = eventRepository.getReferenceById(eventId);

        return SuccessResponse.defaultResponse(EventDto.from(event), "Retrieve Event %s".formatted(eventId));
    }

    @Override
    public SuccessResponse<Boolean> deleteEventById(Long eventId) {
        eventRepository.deleteById(eventId);

        return SuccessResponse.defaultResponse(TRUE, "Event deleted");
    }

    @Override
    public SuccessResponse<List<EventDto>> getAllEventsByUserId() {
        var currentUser = UserContextHolder.getCurrentUser();
        var allEventsByCurrentUser = eventRepository.getAllEventsByUserId(currentUser.userId());
        var events = allEventsByCurrentUser.stream().map(EventDto::from).collect(Collectors.toList());

        return SuccessResponse.defaultResponse(events, "All Events By Current User");
    }

    @Override
    public SuccessResponse<List<EventDto>> getAllOngoingEvents() {
        var currentUser = UserContextHolder.getCurrentUser();
        var allOngoingEvents = eventRepository.getAllOngoingEvents(currentUser.userId());
        var events = allOngoingEvents.stream().map(EventDto::from).collect(Collectors.toList());

        return SuccessResponse.defaultResponse(events, "All Ongoing Events");
    }
}

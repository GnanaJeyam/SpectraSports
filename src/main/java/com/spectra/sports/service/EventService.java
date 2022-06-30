package com.spectra.sports.service;

import com.spectra.sports.dto.EventDto;
import com.spectra.sports.entity.SportsEvent;
import com.spectra.sports.response.SuccessResponse;

import java.util.List;

public interface EventService {

    SuccessResponse<EventDto> addEvent(SportsEvent sportsEvent);

    SuccessResponse<EventDto> getEventById(Long eventId);

    SuccessResponse<List<EventDto>> getAllEventsByUserId();

    SuccessResponse<List<EventDto>> getAllOngoingEvents();
}

package com.spectra.sports.dto;

import com.spectra.sports.entity.SportsEvent;

import java.math.BigDecimal;

public record EventDto(Long eventId, String eventName, String shortDescription,
                       String longDescription, BigDecimal price, String priceType,
                       String bookingLink, String sportType, String location,
                       String eventDate, String eventTime, String[] eventPoster) {

    public static EventDto from(SportsEvent sportsEvent) {
        return new EventDto(
                sportsEvent.getEventId(),
                sportsEvent.getEventName(),
                sportsEvent.getShortDescription(),
                sportsEvent.getLongDescription(),
                sportsEvent.getPrice(),
                sportsEvent.getPriceType(),
                sportsEvent.getBookingLink(),
                sportsEvent.getSportType(),
                sportsEvent.getLocation(),
                sportsEvent.getEventDate(),
                sportsEvent.getEventTime(),
                sportsEvent.getEventPoster()
        );
    }
}

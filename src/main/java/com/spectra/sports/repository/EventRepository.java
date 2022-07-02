package com.spectra.sports.repository;

import com.spectra.sports.entity.SportsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<SportsEvent, Long> {

    @Query(value = "SELECT event from SportsEvent event WHERE event.createdBy = :userId ORDER BY event.eventId DESC")
    List<SportsEvent> getAllEventsByUserId(Long userId);

    @Query(value = "SELECT event from SportsEvent event WHERE event.createdBy <> :userId ORDER BY event.eventId DESC")
    List<SportsEvent> getAllOngoingEvents(Long userId);
}

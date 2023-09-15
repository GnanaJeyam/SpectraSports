package com.spectra.sports.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Table(name = "sports_event")
@EqualsAndHashCode(callSuper=false)
@Data
public class SportsEvent extends BaseEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "price_type")
    private String priceType;

    @Column(name = "booking_link")
    private String bookingLink;

    @Column(name = "sport_type")
    private String sportType;

    @Column(name = "location")
    private String location;

    @Column(name = "event_date")
    private String eventDate;

    @Column(name = "event_time")
    private String eventTime;

    @Type(StringArrayType.class)
    @Convert(attributeName = "eventPoster", converter = StringArrayType.class, disableConversion = true)
    @Column(name = "event_poster_links", columnDefinition = "text[]")
    private String[] eventPoster;
}

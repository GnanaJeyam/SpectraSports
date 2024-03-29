package com.spectra.sports.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "student_mentor_mapping", indexes = {@Index(name = "student_mentor_index", columnList = "student_id, mentor_id")})
@Data
@EqualsAndHashCode(callSuper=false)
public class StudentMentorMapping extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_mentor_mapping_id")
    private Long studentMentorMappingId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "mentor_id")
    private Long mentorId;

    @Column(name = "sport_type")
    private String sportType;

    @Column(name = "plan_info")
    private String plan;

    @Column(name = "slot")
    private String slot;

    @Column(name = "slot_days")
    private String slotDays;

    @Column(name = "mentor_type")
    private String mentorType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "expired")
    private Boolean expired;

    @Column(name = "tagged")
    private Boolean tagged;
}
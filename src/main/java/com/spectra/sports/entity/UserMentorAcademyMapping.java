package com.spectra.sports.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_mentor_academy_mapping")
@Data
@EqualsAndHashCode(callSuper=false)
public class UserMentorAcademyMapping extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_mentor_academy_mapping_id")
    private Long userMentorAcademyMappingId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "mentor_id")
    private Long mentorId;

    @Column(name = "academy_id")
    private Long academyId;

    @Column(name = "sport_type")
    private String sportType;

    @Column(name = "plan_info")
    private String plan;

    @Column(name = "slot")
    private String slot;

    @Column(name = "mentor_type")
    private String mentorType;

    @Column(name = "academy_type")
    private String academyType;

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
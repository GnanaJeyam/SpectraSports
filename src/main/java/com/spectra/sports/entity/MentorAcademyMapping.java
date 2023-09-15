package com.spectra.sports.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "mentor_academy_mapping", indexes = {@Index(name = "mentor_academy_index", columnList = "mentor_id, academy_id")})
@Data
@EqualsAndHashCode(callSuper=false)
public class MentorAcademyMapping extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_mentor_academy_mapping_id")
    private Long mentorAcademyMappingId;

    @Column(name = "mentor_id")
    private Long mentorId;

    @Column(name = "academy_id")
    private Long academyId;

    @Column(name = "mentor_type")
    private String mentorType;

    @Column(name = "academy_type")
    private String academyType;

    @Column(name = "tagged")
    private Boolean tagged;
}
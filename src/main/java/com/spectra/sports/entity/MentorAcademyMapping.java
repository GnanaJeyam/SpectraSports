package com.spectra.sports.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "mentor_academy_mapping")
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
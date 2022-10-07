package com.spectra.sports.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

@Data
@TypeDefs({ @TypeDef(name = "json", typeClass = JsonType.class) })
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "student_details")
public class StudentRatingDetail extends BaseEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column(name = "student_rating_detail_id")
    private Long studentRatingDetailId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "mentor_id")
    private Long mentorId;

    @Column(name = "academy_id")
    private Long academyId;

    @Column(name = "student_id")
    private Long studentId;

    @Type(type = "json")
    @Column(name = "attendances", columnDefinition = "json")
    private List<Attendance> attendances;

    @Type(type = "json")
    @Column(name = "performances", columnDefinition = "json")
    private List<Performance> performances;

    @Column(name = "rating")
    private String rating;

    @Column(name = "slot")
    private String slot;

    @Column(name = "slot_days")
    private String slotDays;

    @Column(name = "academy_name")
    private String academyName;
}
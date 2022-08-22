package com.spectra.sports.entity;

import com.spectra.sports.subscription.SubscriptionInfo;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table( name = "users", uniqueConstraints = {
        @UniqueConstraint( columnNames = {"mobile_number"}),
        @UniqueConstraint( columnNames = {"email"})
})
@TypeDefs({
    @TypeDef( name = "json", typeClass = JsonType.class),
    @TypeDef( name = "string-array", typeClass = StringArrayType.class)
})
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column(name = "user_id")
    private Long userId;

    @Transient
    private boolean isMapped;

    @Transient
    private SubscriptionInfo subscriptionInfo;

    @Column(name = "first_name", length = 30)
    private String firstName;

    @Column(name = "last_name", length = 20)
    private String lastName;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "academy_name", length = 150)
    private String academyName;

    @Column(name = "password", length = 90)
    private String password;

    @Column(name = "mobile_number", length = 12)
    private String mobileNumber;

    @ManyToMany
    @JoinTable(name = "USER_ROLES",
        joinColumns = {
            @JoinColumn(name = "user_id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "role_id")
       }
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "location", length = 30)
    private String location;

    @Column(name = "latitude", length = 30)
    private Double latitude;

    @Column(name = "longitude", length = 30)
    private Double longitude;

    @Column(name = "otp", length = 6)
    private String otp;

    @Column(name = "is_verified")
    @ColumnDefault("false")
    private Boolean isVerified;

    @Column(name = "experience", length = 30)
    private String experience;

    @Column(name = "description", length = 250)
    private String description;

    @Type(type = "string-array")
    @Column(name = "specialistin", columnDefinition = "text[]")
    private String[] specialistIn;

    @Type(type = "json")
    @Column(name = "available_slots", columnDefinition = "jsonb")
    private List<Slot> availableSlots = new ArrayList<>();

    @Column(name = "image_name", length = 250)
    private String imageName;
}

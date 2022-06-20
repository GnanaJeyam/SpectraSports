//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {@UniqueConstraint(
                columnNames = {"mobile_number"}
        ), @UniqueConstraint(
                columnNames = {"email"}
        )}
)
@TypeDefs({@TypeDef(
        name = "json",
        typeClass = JsonType.class
), @TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
)})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "user_id"
    )
    private Long userId;
    @Column(
            name = "first_name",
            length = 30
    )
    private String firstName;
    @Column(
            name = "last_name",
            length = 20
    )
    private String lastName;
    @Column(
            name = "email",
            length = 50
    )
    private String email;
    @Column(
            name = "password",
            length = 90
    )
    private String password;
    @Column(
            name = "mobile_number",
            length = 12
    )
    private String mobileNumber;
    @ManyToMany
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = {@JoinColumn(
                    name = "user_id"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "role_id"
            )}
    )
    private Set<Role> roles = new HashSet();
    @Column(
            name = "location",
            length = 30
    )
    private String location;
    @Column(
            name = "latitude",
            length = 30
    )
    private String latitude;
    @Column(
            name = "longitude",
            length = 30
    )
    private String longitude;
    @Column(
            name = "otp",
            length = 6
    )
    private String otp;
    @Column(
            name = "is_verified"
    )
    @ColumnDefault("false")
    private Boolean isVerified;
    @Column(
            name = "experience",
            length = 30
    )
    private String experience;
    @Column(
            name = "description",
            length = 250
    )
    private String description;
    @Type(
            type = "string-array"
    )
    @Column(
            name = "specialistin",
            columnDefinition = "text[]"
    )
    private String[] specialistIn;
    @Type(
            type = "json"
    )
    @Column(
            name = "available_slots",
            columnDefinition = "json"
    )
    private List<Slot> availableSlots;
    @Column(
            name = "image_name",
            length = 250
    )
    private String imageName;

    public User() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean getVerified() {
        return this.isVerified;
    }

    public void setVerified(Boolean verified) {
        this.isVerified = verified;
    }

    public String getOtp() {
        return this.otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getExperience() {
        return this.experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getSpecialistIn() {
        return this.specialistIn;
    }

    public void setSpecialistIn(String[] specialistIn) {
        this.specialistIn = specialistIn;
    }

    public List<Slot> getAvailableSlots() {
        return this.availableSlots;
    }

    public void setAvailableSlots(List<Slot> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public String getImageName() {
        return this.imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

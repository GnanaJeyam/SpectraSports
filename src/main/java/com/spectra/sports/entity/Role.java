package com.spectra.sports.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", length = 20)
    private RoleType roleType;

    @Column(name = "priority")
    private Byte rolePriority;

    public Role() {}

    public Role(Long roleId, RoleType roleType, Byte rolePriority) {
        this.roleId = roleId;
        this.roleType = roleType;
        this.rolePriority = rolePriority;
        LocalDateTime createdAt = this.getCreatedAt();
        this.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
        this.setCreatedBy(0L);
        this.setUpdatedBy(0L);
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public RoleType getRoleType() {
        return this.roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Byte getRolePriority() {
        return this.rolePriority;
    }

    public void setRolePriority(Byte rolePriority) {
        this.rolePriority = rolePriority;
    }
}

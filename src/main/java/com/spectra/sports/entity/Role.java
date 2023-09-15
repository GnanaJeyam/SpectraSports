package com.spectra.sports.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {
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

    @Override
    public String getAuthority() {

        return roleType.name();
    }
}

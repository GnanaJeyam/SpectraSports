package com.spectra.sports.repository;

import com.spectra.sports.entity.Role;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("select role from Role role where role.id IN (:roleIds)")
    Set<Role> getRolesByIds(Set<Long> roleIds);
}

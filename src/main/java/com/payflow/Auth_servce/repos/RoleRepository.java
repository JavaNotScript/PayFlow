package com.payflow.Auth_servce.repos;

import com.payflow.Auth_servce.models.RoleType;
import com.payflow.Auth_servce.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<UserRole,Long> {
    Optional<UserRole> findByRoleName(RoleType roleType);
}

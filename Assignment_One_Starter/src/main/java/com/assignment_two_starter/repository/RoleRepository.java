package com.assignment_two_starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.assignment_two_starter.model.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {


    Optional<Role> findByRoleName(String roleName);

}

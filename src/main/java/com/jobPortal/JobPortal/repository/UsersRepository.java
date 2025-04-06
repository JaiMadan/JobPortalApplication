package com.jobPortal.JobPortal.repository;

import com.jobPortal.JobPortal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    @Query("select u from Users u where u.email = :email")
    Optional<Users> findByEmail(@Param("email") String email);
}

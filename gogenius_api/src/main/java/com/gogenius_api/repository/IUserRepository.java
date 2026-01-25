package com.gogenius_api.repository;

import com.gogenius_api.repository.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, String>{
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:input) OR LOWER(u.login) = LOWER(:input)")
    User findByEmailOrLoginIgnoreCase(@Param("input") String input);
}

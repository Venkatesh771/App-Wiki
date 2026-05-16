package com.example.dictionary.repository;

import com.example.dictionary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCwid(String cwid);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}

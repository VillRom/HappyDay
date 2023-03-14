package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanchev.happyday.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}

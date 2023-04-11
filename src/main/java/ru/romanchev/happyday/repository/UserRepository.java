package ru.romanchev.happyday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.romanchev.happyday.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u.id from User u where u.id <> ?1")
    List<Long> findAllUsersId(Long admin);

    @Query("select u from User u where u.id <> ?1")
    List<User> findAllByIdNot(Long admin);
}

package ru.romanchev.happyday.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private String nikName;

    private Long chatId;
}

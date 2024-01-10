package ru.romanchev.happyday.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "jokes")
public class Joke {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String textJoke;
}

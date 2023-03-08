package ru.romanchev.happyday.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "message")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "request_text")
    private String textIn;

    @Column(name = "response_text")
    private String textTo;

    @Column(name = "date_message")
    private Long date;
}

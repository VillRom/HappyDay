package ru.romanchev.happyday.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "phrases")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Phrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String textPhrase;

    public void setTextPhrase(String textPhrase) {
        this.textPhrase = textPhrase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Phrase phrase = (Phrase) o;
        return getId() != null && Objects.equals(getId(), phrase.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

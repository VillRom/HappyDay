package ru.romanchev.happyday.repository;

import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Data
public class HappyRepository {

    private List<String> happyPhrases = new ArrayList<>();
}

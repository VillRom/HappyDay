package ru.romanchev.happyday.service;

import ru.romanchev.happyday.dto.PhraseDto;
import ru.romanchev.happyday.model.Phrase;

import java.util.List;

public interface PhraseService {

    void addPhrase(PhraseDto dto);

    void addPhrases(List<Phrase> phrases);

    PhraseDto getPhrase(Long phraseId);

    PhraseDto getRandomPhrase();

    String updatePhrases();
}

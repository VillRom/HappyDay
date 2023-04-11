package ru.romanchev.happyday.mapper;

import lombok.experimental.UtilityClass;
import ru.romanchev.happyday.dto.PhraseDto;
import ru.romanchev.happyday.model.Phrase;

@UtilityClass
public class PhraseMapper {

    public static Phrase dtoToPhrase(PhraseDto dto) {
        Phrase phrase = new Phrase();
        phrase.setId(dto.getId());
        phrase.setTextPhrase(dto.getTextPhrase());
        return phrase;
    }

    public static PhraseDto phraseToDto(Phrase phrase) {
        PhraseDto dto = new PhraseDto();
        dto.setId(phrase.getId());
        dto.setTextPhrase(phrase.getTextPhrase());
        return dto;
    }
}

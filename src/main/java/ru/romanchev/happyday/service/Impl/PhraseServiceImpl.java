package ru.romanchev.happyday.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.romanchev.happyday.dto.PhraseDto;
import ru.romanchev.happyday.mapper.PhraseMapper;
import ru.romanchev.happyday.model.Phrase;
import ru.romanchev.happyday.repository.PhrasesRepository;
import ru.romanchev.happyday.service.PhraseService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PhraseServiceImpl implements PhraseService {

    private final PhrasesRepository phrasesRepository;

    private final File file;

    @Autowired
    public PhraseServiceImpl(PhrasesRepository phrasesRepository) {
        this.phrasesRepository = phrasesRepository;
        this.file = new File("Phrases.txt");
    }

    @Override
    public void addPhrase(PhraseDto dto) {
        phrasesRepository.save(PhraseMapper.dtoToPhrase(dto));
    }

    @Override
    public void addPhrases(List<Phrase> phrases) {
        if (phrases.isEmpty()) {
            throw new RuntimeException();
        }
        phrasesRepository.saveAll(phrases);
    }

    @Override
    public PhraseDto getPhrase(Long phraseId) {
        return PhraseMapper.phraseToDto(phrasesRepository.findById(phraseId).orElseThrow());
    }

    private List<Phrase> createPhrases(List<String> phrases) {
        List<Phrase> phraseList = new ArrayList<>();
        for (String text : phrases) {
            Phrase phrase = new Phrase();
            phrase.setTextPhrase(text);
            phraseList.add(phrase);
        }
        return phraseList;
    }

    private List<String> readFileWithPhrases(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> newPhrases = new ArrayList<>();
            while (br.ready()) {
                newPhrases.add(br.readLine());
            }
            return newPhrases;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PhraseDto getRandomPhrase() {
        if (phrasesRepository.count() == 0) {
            addPhrases(createPhrases(readFileWithPhrases(file)));
        }
        return PhraseMapper.phraseToDto(phrasesRepository.findById(randomLong()).orElseThrow());
    }

    @Override
    public String updatePhrases() {
        long countPhrasesFromBd = phrasesRepository.count();
        if (countPhrasesFromBd == 0) {
            addPhrases(createPhrases(readFileWithPhrases(file)));
            return "Фразы добалены в БД";
        }
        List<String> phrases = readFileWithPhrases(file);
        Phrase lastPhrase = phrasesRepository.findById(countPhrasesFromBd).orElseThrow();
        phrases.removeAll(phrases.subList(0, phrases.indexOf(lastPhrase.getTextPhrase()) + 1));
        if (phrases.isEmpty()) {
            return "Новые фразы не найдены";
        }
        addPhrases(createPhrases(phrases));
        return "Новые фразы добавлены в БД";
    }

    private Long randomLong() {
        return (long) new Random().nextInt((int) phrasesRepository.count() + 1);
    }
}

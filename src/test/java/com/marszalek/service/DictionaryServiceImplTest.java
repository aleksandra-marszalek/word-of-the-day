package com.marszalek.service;

import com.marszalek.client.DatamuseApiClient;
import com.marszalek.client.DictionaryApiClient;
import com.marszalek.client.WiktionaryApiClient;
import com.marszalek.dto.DatamuseWordDTO;
import com.marszalek.dto.DictionarySingleWordResponseDTO;
import com.marszalek.dto.PhoneticDTO;
import com.marszalek.dto.WiktionaryDefinitionDTO;
import com.marszalek.dto.WiktionaryEntryDTO;
import com.marszalek.error.DefinitionFetchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictionaryServiceImplTest {

    @InjectMocks
    private DictionaryServiceImpl service;

    @Mock
    private WiktionaryApiClient wiktionary;

    @Mock
    private DatamuseApiClient datamuse;

    @Mock
    private DictionaryApiClient dictionaryApi;

    private static Map<String, List<WiktionaryEntryDTO>> wiktionaryResponse(String pos, String definition) {
        return Map.of("en", List.of(new WiktionaryEntryDTO(pos, "English",
                List.of(new WiktionaryDefinitionDTO(definition)))));
    }

    @Test
    void usesWiktionary_stripsHtmlAndLowercasesPartOfSpeech() {
        when(wiktionary.getDefinition("kinship")).thenReturn(wiktionaryResponse("Noun",
                "<a rel=\"mw:WikiLink\" href=\"/wiki/relation\">Relation</a> or connection &amp; bond."));
        when(dictionaryApi.getDefinition("kinship")).thenReturn(List.of());

        var data = service.getWordDefinitionData("kinship");

        assertEquals("Relation or connection & bond.", data.definition());
        assertEquals("noun", data.partOfSpeech());
        assertEquals("", data.phonetic());
        assertEquals("", data.audioUrl());
        verify(datamuse, never()).lookup(anyString(), anyString(), anyInt());
    }

    @Test
    void fallsBackToDatamuse_whenWiktionaryFails() {
        when(wiktionary.getDefinition("kinship")).thenThrow(new RuntimeException("boom"));
        when(datamuse.lookup("kinship", "d", 1)).thenReturn(List.of(new DatamuseWordDTO("kinship",
                List.of("n"), List.of("n\tRelation or connection by blood, marriage or adoption. "))));
        when(dictionaryApi.getDefinition("kinship")).thenThrow(new RuntimeException("blocked"));

        var data = service.getWordDefinitionData("kinship");

        assertEquals("Relation or connection by blood, marriage or adoption.", data.definition());
        assertEquals("noun", data.partOfSpeech());
    }

    @Test
    void fallsBackToDatamuse_whenWiktionaryHasNoEnglishEntry() {
        when(wiktionary.getDefinition("zzz")).thenReturn(Map.of("fr", List.of()));
        when(datamuse.lookup("zzz", "d", 1)).thenReturn(List.of(new DatamuseWordDTO("zzz",
                List.of("v"), List.of("v\tTo sleep"))));
        when(dictionaryApi.getDefinition(any())).thenReturn(List.of());

        var data = service.getWordDefinitionData("zzz");

        assertEquals("To sleep", data.definition());
        assertEquals("verb", data.partOfSpeech());
    }

    @Test
    void throwsDefinitionFetchException_whenAllDefinitionSourcesFail() {
        when(wiktionary.getDefinition("qqq")).thenThrow(new RuntimeException("not json"));
        when(datamuse.lookup("qqq", "d", 1)).thenReturn(List.of());

        assertThrows(DefinitionFetchException.class, () -> service.getWordDefinitionData("qqq"));
    }

    @Test
    void ignoresDatamuseResultsForADifferentWord() {
        when(wiktionary.getDefinition("cat")).thenThrow(new RuntimeException("down"));
        when(datamuse.lookup("cat", "d", 1)).thenReturn(List.of(new DatamuseWordDTO("cats",
                List.of("n"), List.of("n\tplural of cat"))));

        assertThrows(DefinitionFetchException.class, () -> service.getWordDefinitionData("cat"));
    }

    @Test
    void addsPronunciation_whenDictionaryApiAvailable() {
        when(wiktionary.getDefinition("cat")).thenReturn(wiktionaryResponse("Noun", "A small feline."));
        when(dictionaryApi.getDefinition("cat")).thenReturn(List.of(new DictionarySingleWordResponseDTO("cat",
                List.of(new PhoneticDTO("", ""), new PhoneticDTO("/kæt/", "https://example.org/cat.mp3")), List.of())));

        var data = service.getWordDefinitionData("cat");

        assertEquals("/kæt/", data.phonetic());
        assertEquals("https://example.org/cat.mp3", data.audioUrl());
    }

    @Test
    void stillReturnsDefinition_whenDictionaryApiReturnsNonJson() {
        when(wiktionary.getDefinition("cat")).thenReturn(wiktionaryResponse("Noun", "A small feline."));
        // e.g. the Cloudflare "Just a moment..." HTML page failing to deserialise
        when(dictionaryApi.getDefinition("cat")).thenThrow(new IllegalStateException("Unexpected character '<'"));

        var data = service.getWordDefinitionData("cat");

        assertEquals("A small feline.", data.definition());
        assertEquals("", data.phonetic());
    }
}

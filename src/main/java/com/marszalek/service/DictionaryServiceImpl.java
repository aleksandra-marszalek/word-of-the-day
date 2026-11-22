package com.marszalek.service;

import com.marszalek.client.DatamuseApiClient;
import com.marszalek.client.DictionaryApiClient;
import com.marszalek.client.WiktionaryApiClient;
import com.marszalek.dto.DatamuseWordDTO;
import com.marszalek.dto.PhoneticDTO;
import com.marszalek.dto.WiktionaryEntryDTO;
import com.marszalek.error.DefinitionFetchException;
import com.marszalek.model.DictionaryData;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Looks up a definition from Wiktionary, falling back to Datamuse. Phonetics and audio are a
 * best-effort extra from dictionaryapi.dev, which now sits behind a bot challenge and often fails
 * from AWS, so a failure there must never prevent the word of the day from being stored.
 */
@Slf4j
@Singleton
public class DictionaryServiceImpl implements DictionaryService {

    record Core(String definition, String partOfSpeech) {
    }

    private record Pronunciation(String text, String audio) {
        static final Pronunciation NONE = new Pronunciation("", "");
    }

    private final WiktionaryApiClient wiktionaryApiClient;
    private final DatamuseApiClient datamuseApiClient;
    private final DictionaryApiClient dictionaryApiClient;

    public DictionaryServiceImpl(WiktionaryApiClient wiktionaryApiClient,
                                 DatamuseApiClient datamuseApiClient,
                                 DictionaryApiClient dictionaryApiClient) {
        this.wiktionaryApiClient = wiktionaryApiClient;
        this.datamuseApiClient = datamuseApiClient;
        this.dictionaryApiClient = dictionaryApiClient;
    }

    @Override
    public DictionaryData getWordDefinitionData(String word) {
        var core = lookupWiktionary(word)
                .or(() -> lookupDatamuse(word))
                .orElseThrow(() -> new DefinitionFetchException(format("No definition found for word: %s", word)));

        var pronunciation = lookupPronunciation(word);
        return new DictionaryData(core.definition(), core.partOfSpeech(), pronunciation.text(), pronunciation.audio());
    }

    private Optional<Core> lookupWiktionary(String word) {
        try {
            var entries = wiktionaryApiClient.getDefinition(word).get("en");
            return parseWiktionary(entries);
        } catch (Exception e) {
            log.warn("Wiktionary lookup failed for '{}': {}", word, e.toString());
            return Optional.empty();
        }
    }

    private Optional<Core> lookupDatamuse(String word) {
        try {
            return parseDatamuse(word, datamuseApiClient.lookup(word, "d", 1));
        } catch (Exception e) {
            log.warn("Datamuse lookup failed for '{}': {}", word, e.toString());
            return Optional.empty();
        }
    }

    private Pronunciation lookupPronunciation(String word) {
        try {
            var response = dictionaryApiClient.getDefinition(word).stream().findFirst();
            if (response.isEmpty() || response.get().phonetics() == null) {
                return Pronunciation.NONE;
            }
            var phonetics = response.get().phonetics();
            return new Pronunciation(
                    firstNonBlank(phonetics, PhoneticDTO::text),
                    firstNonBlank(phonetics, PhoneticDTO::audio));
        } catch (Exception e) {
            log.info("Pronunciation lookup skipped for '{}': {}", word, e.toString());
            return Pronunciation.NONE;
        }
    }

    private static String firstNonBlank(List<PhoneticDTO> phonetics,
                                        java.util.function.Function<PhoneticDTO, String> field) {
        return phonetics.stream()
                .map(field)
                .filter(s -> s != null && !s.isBlank())
                .findFirst()
                .orElse("");
    }

    static Optional<Core> parseWiktionary(List<WiktionaryEntryDTO> entries) {
        if (entries == null) {
            return Optional.empty();
        }
        for (var entry : entries) {
            if (entry.definitions() == null) {
                continue;
            }
            for (var def : entry.definitions()) {
                var text = stripHtml(def.definition());
                if (!text.isBlank()) {
                    return Optional.of(new Core(text, lower(entry.partOfSpeech())));
                }
            }
        }
        return Optional.empty();
    }

    static Optional<Core> parseDatamuse(String word, List<DatamuseWordDTO> results) {
        if (results == null) {
            return Optional.empty();
        }
        return results.stream()
                .filter(r -> r.word() != null && r.word().equalsIgnoreCase(word) && r.defs() != null)
                .flatMap(r -> r.defs().stream())
                .map(d -> {
                    // Datamuse definitions look like "n<TAB>some definition"
                    int tab = d.indexOf('\t');
                    var pos = tab > 0 ? expandPartOfSpeech(d.substring(0, tab)) : "";
                    var text = stripHtml(tab >= 0 ? d.substring(tab + 1) : d);
                    return new Core(text, pos);
                })
                .filter(c -> !c.definition().isBlank())
                .findFirst();
    }

    private static String expandPartOfSpeech(String code) {
        return switch (code.trim()) {
            case "n" -> "noun";
            case "v" -> "verb";
            case "adj" -> "adjective";
            case "adv" -> "adverb";
            default -> "";
        };
    }

    static String stripHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("<[^>]*>", "")
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String lower(String s) {
        return s == null ? "" : s.trim().toLowerCase(java.util.Locale.ROOT);
    }
}

package ru.rkhamatyarov.entity;

import net.andreinc.markovneat.MChainText;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.types.enums.RandomType;
import net.andreinc.mockneat.unit.text.Markovs;
import net.andreinc.mockneat.utils.file.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rkhamatyarov.util.CustomFileManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.andreinc.mockneat.utils.ValidationUtils.notNull;

public class CustomMarkovChain extends Markovs {
    private static final Logger log = LoggerFactory.getLogger(CustomMarkovChain.class);

    private static final FileManager fileManager = FileManager.getInstance();
    private static final CustomFileManager customFileManager = CustomFileManager.getInstance();
    private static final String RUS_TEXT_MARKOV_CHAIN = "textOnRussianLanguage";

    private final Map<String, MChainText> markovUnits =  new ConcurrentHashMap <String, MChainText> ();

    public CustomMarkovChain(MockNeat mockNeat) {
        super(mockNeat);
    }

    public static CustomMarkovChain customMarkovChain() {
        return new CustomMarkovChain(new MockNeat(RandomType.THREAD_LOCAL));
    }

    public CustomMarkovChain fromFile(String path, int noStates) {
        notNull(path, "path");

        try {
            MChainText mChainText = new MChainText(noStates);
            List<String> lines = fileManager.read(path).stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());;

            mChainText.train(lines);

            markovUnits.put(RUS_TEXT_MARKOV_CHAIN, mChainText);
        } catch (IOException e) {
            log.error("Cannot load text from '{}'.", path);
            throw new UncheckedIOException(e);
        }
        return this;
    }


    public Boolean generateTextToFile(String path, int approximateLength) {
        notNull(path, "path");
        notNull(markovUnits.get(RUS_TEXT_MARKOV_CHAIN), "content");

        try {
            return customFileManager.write(path, markovUnits.get(RUS_TEXT_MARKOV_CHAIN).generateText(approximateLength * (getWordLength() + 1)));
        } catch (IOException e) {
            log.error("Cannot write text to '{}'.", path);
            throw new UncheckedIOException(e);
        }
    }

    public String generateText(int approximateLength) {
        notNull(markovUnits.get(RUS_TEXT_MARKOV_CHAIN), "content");

        return markovUnits.get(RUS_TEXT_MARKOV_CHAIN).generateText(approximateLength * (getWordLength() + 1));
    }

    private int getWordLength() {
        notNull(markovUnits.get(RUS_TEXT_MARKOV_CHAIN), "map hasn't chain");
        return markovUnits.get(RUS_TEXT_MARKOV_CHAIN).generateText(1).split("[\\p{Punct}\\s]+")[1].length();
    }

    public static void main(String[] args) {
        System.out.printf("File is written: %s\n\r", customMarkovChain().fromFile("in.txt", 2).generateTextToFile("out.txt", 44 * 4));
        System.out.printf("%s\n\r", customMarkovChain().fromFile("in.txt", 2).generateText( 44));
        System.out.printf("%s\n\r", customMarkovChain()
                .fromFile("in.txt", 2)
                .generateText( 44)
                .split(" ").length);
    }
}

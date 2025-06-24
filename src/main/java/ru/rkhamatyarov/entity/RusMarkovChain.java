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

/**
 * The RusMarkovChain class provides methods
 * to generate text using a Markov chain.
 */
public final class RusMarkovChain extends Markovs {

    /**
     * Logger for the RusMarkovChain class.
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(RusMarkovChain.class);

    /**
     * A singleton instance of the FileManager
     * for file operations.
     */
    private static final FileManager FILE_MANAGER = FileManager.getInstance();

    /**
     * A singleton instance of the CustomFileManager
     * for custom file operations.
     */
    private static final CustomFileManager CUSTOM_FILE_MANAGER
            = CustomFileManager.getInstance();

    /**
     * The key used to store the Markov chain text in the map.
     */
    private static final String RUS_TEXT_MARKOV_CHAIN = "RUS_TEXT_MARKOV_CHAIN";

    /**
     * A map that holds the Markov chain text units.
     */
    private final Map<String, MChainText> markovUnits
            = new ConcurrentHashMap<>();

    /**
     * Constructs a RusMarkovChain instance
     * with the specified MockNeat instance.
     *
     * @param mockNeat the MockNeat instance
     */
    public RusMarkovChain(final MockNeat mockNeat) {
        super(mockNeat);
    }

    /**
     * Creates a new instance of RusMarkovChain
     * with a default MockNeat instance.
     *
     * @return a new RusMarkovChain instance
     */
    public static RusMarkovChain rusMarkovChain() {
        return new RusMarkovChain(new MockNeat(RandomType.THREAD_LOCAL));
    }

    /**
     * Loads text from a file and trains the Markov chain.
     *
     * @param path     the path to the file
     * @param noStates the number of states for the Markov chain
     * @return the current RusMarkovChain instance
     */
    public RusMarkovChain fromFile(final String path, final int noStates) {
        notNull(path, "path");

        try {
            MChainText mChainText = new MChainText(noStates);
            List<String> lines = FILE_MANAGER.read(path).stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            mChainText.train(lines);

            markovUnits.put(RUS_TEXT_MARKOV_CHAIN, mChainText);
        } catch (IOException e) {
            LOG.error("Cannot load text from '{}'.", path);
            throw new UncheckedIOException(e);
        }
        return this;
    }

    /**
     * Generates text using the Markov chain
     * and writes it to the specified file.
     *
     * @param path              the path to the file where
     *                          the generated text will be written
     * @param approximateLength the approximate length
     *                          of the generated text in words
     * @return true if the text
     * was successfully written to the file, false otherwise
     */
    public Boolean generateTextToFile(
            final String path,
            final int approximateLength
    ) {
        notNull(path, "path");
        notNull(markovUnits.get(RUS_TEXT_MARKOV_CHAIN), "content");

        try {
            return CUSTOM_FILE_MANAGER.write(path, markovUnits
                    .get(RUS_TEXT_MARKOV_CHAIN)
                    .generateText(approximateLength * (getWordLength() + 1)));

        } catch (IOException e) {
            LOG.error("Cannot write text to '{}'.", path);
            throw new UncheckedIOException(e);
        }
    }


    /**
     * Generates text using the Markov chain
     * with the specified approximate length.
     *
     * @param approximateLength the approximate length
     *                          of the generated text in words
     * @return the generated text as a String
     */
    public String generateText(final int approximateLength) {
        notNull(markovUnits.get(RUS_TEXT_MARKOV_CHAIN), "content");

        return markovUnits
                .get(RUS_TEXT_MARKOV_CHAIN)
                .generateText(approximateLength * (getWordLength() + 1));
    }

    /**
     * Calculates the average word length based on the generated text.
     *
     * @return the length of a word in the generated text
     */
    private int getWordLength() {
        notNull(markovUnits.get(RUS_TEXT_MARKOV_CHAIN), "map hasn't chain");
        return markovUnits
                .get(RUS_TEXT_MARKOV_CHAIN)
                .generateText(1)
                .split("[\\p{Punct}\\s]+")[0].length();
    }
}

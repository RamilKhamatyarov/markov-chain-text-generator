package ru.rkhamatyarov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.rkhamatyarov.entity.RusMarkovChain.rusMarkovChain;

public final class MarkovChainTextGeneratorApp {

    /**
     * Logger for the MarkovChainTextGeneratorApp class.
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(MarkovChainTextGeneratorApp.class);

    /**
     * Minimum value for N.
     */
    private static final int MIN_N = 1;

    /**
     * Maximum value for N.
     */
    private static final int MAX_N = 20;

    /**
     * Minimum value for L.
     */
    private static final long MIN_L = 1;

    /**
     * Argument index of the input file.
     */
    private static final int ARG_INDEX_INPUT_FILE = 0;

    /**
     * Argument index of the output file.
     */
    private static final int ARG_INDEX_OUTPUT_FILE = 1;

    /**
     * Argument index of the n variable.
     */
    private static final int ARG_INDEX_N = 2;

    /**
     * Argument index of the l variable.
     */
    private static final int ARG_INDEX_L = 3;

    private MarkovChainTextGeneratorApp() {
        throw new UnsupportedOperationException(
                "Utility class should not be instantiated"
        );
    }

    /**
     * The main method that runs the text generation application.
     *
     * @param args command line arguments:
     *             input file name, output file name, N, and L.
     */
    public static void main(final String[] args) {
        String inputFileName = args[ARG_INDEX_INPUT_FILE];
        String outputFileName = args[ARG_INDEX_OUTPUT_FILE];

        final int n = Integer.parseInt(args[ARG_INDEX_N]);
        checkRangeOfN(n);

        final int l = Integer.parseInt(args[ARG_INDEX_L]);
        checkRangeOfL(l);

        Boolean isSuccessWroteOut = rusMarkovChain()
                .fromFile(inputFileName, n)
                .generateTextToFile(outputFileName, l);

        if (isSuccessWroteOut && LOG.isDebugEnabled()) {
            LOG.debug("Text wrote successfully on the file: " + outputFileName);
        }

        if (!isSuccessWroteOut) {
            throw new IllegalStateException(
                    "Generated text didn't wrote on file " + outputFileName
            );
        }
    }

    private static void checkRangeOfN(final int n) {
        if (n < MIN_N || n >= MAX_N) {
            throw new Error("N out of range (" + MIN_N + ", " + MAX_N + ")");
        }
    }

    private static void checkRangeOfL(final long l) {
        if (l < MIN_L || l >= Long.MAX_VALUE) {
            throw new Error("L out of range (" + MIN_L + ", Long.MAX_VALUE)");
        }
    }

}

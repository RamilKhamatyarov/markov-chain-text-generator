package ru.rkhamatyarov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

import static ru.rkhamatyarov.entity.RusMarkovChain.rusMarkovChain;


public class MarkovChainTextGeneratorApp {
    private static final Logger log = LoggerFactory.getLogger(MarkovChainTextGeneratorApp.class);
    private static Random r = new Random();

    public static void main(String[] args) throws IOException {
        String inputFileName = args[0];
        String outputFileName = args[1];

        final int N = Integer.parseInt(args[2]);
        checkRangeOfN(N);

        final int L = Integer.parseInt(args[3]);
        checkRangeOfL(L);

        Boolean isSuccessWroteOut = rusMarkovChain().fromFile(inputFileName, N).generateTextToFile(outputFileName, L);

        if (isSuccessWroteOut && log.isDebugEnabled()) {
            log.debug("Text wrote successfully in the file: " + outputFileName);
        }
    }

    private static void checkRangeOfN(int N) {
        if (N <= 0 || N >= 20) {
            throw new Error("N out of range (0, 20)");
        }
    }

    private static void checkRangeOfL(long L) {
        if (L <= 0 || L >= Long.MAX_VALUE) {
            throw new Error("L out of range (0, Long.MAX_VALUE)");
        }
    }

}

package ru.rkhamatyarov;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

public class MarkovChainTextGeneratorApp {
    private static final Logger log = LoggerFactory.getLogger(MarkovChainTextGeneratorApp.class);
    private static Random r = new Random();

    public static void main(String[] args) throws IOException {
        String inputFileName = args[0];
        String outputFileName = args[1];

        final int N = Integer.parseInt(args[2]);
        checkRangeOfN(N);

        final long L = Long.parseLong(args[3]);
        checkRangeOfL(L);

        String res = markov(inputFileName, N, L);

        writeFile(outputFileName, res);
        log.debug("Result: {}", res);

        log.info("Text wrote successfully in the file: " + outputFileName);
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

    /**
     * Markov chain text generator
     *
     * @param filePath
     * @param keySize
     * @param outputSize
     * @return
     * @throws IOException
     * @ref https://rosettacode.org/wiki/Markov_chain_text_generator#Java
     */
    private static String markov(String filePath, int keySize, long outputSize) throws IOException {
        if (keySize < 1) {
            throw new IllegalArgumentException("Key size can't be less than 1");
        }

        String[] words = readFile(filePath);

        if (outputSize < keySize) {
            throw new Error("Output size is out of range");
        }

        if (words.length == 0) {
            throw new Error("Input file is empty");
        }
        Map<String, List<String>> dict = new HashMap<>();

        for (int i = 0; i < (words.length - keySize); ++i) {
            if (words[i].equals("")) {
                continue;
            }

            StringBuilder key = new StringBuilder(words[i]);
            for (int j = i + 1; j < i + keySize; ++j) {
                if (words[j].equals("")) {
                    continue;
                }

                if (words[j].equals(".") || words[j].equals(",")) {
                    key.append(words[j]);
                } else {
                    key.append(' ').append(words[j]);
                }

            }

            if (words[i + keySize].equals("")) {
                continue;
            }

            String value = (i + keySize < words.length) ? words[i + keySize] : "";
            if (!dict.containsKey(key.toString())) {
                ArrayList<String> list = new ArrayList<>();
                list.add(value);
                dict.put(key.toString(), list);
            } else {
                dict.get(key.toString()).add(value);
            }
        }

        int n = 0;
        int breakCount = 0;

        StringBuilder prefix = getRandomKeyByDistribution(keySize, words, new StringBuilder());
//         checkProbabilityDistribution(words);
        List<String> output = new ArrayList<>(Arrays.asList(prefix.toString().split(" ")));

        while (true) {
            if (!dict.containsKey(prefix.toString())) {
                prefix = getRandomKeyByDistribution(keySize, words, prefix);
                breakCount ++;

                if (breakCount > 1_000_000) {
                    throw new Error("Generated random word not found on the dictionary with probability paths.");
                }

                continue;
            }
            breakCount = 0;

            List<String> suffix = dict.get(prefix.toString());

            if (suffix.size() == 1) {
                // Get item from list with one element
                if (Objects.equals(suffix.get(0), "")) {
                    return output.stream()
                            .reduce("", (a, b) -> a + " " + b);
                }
                output.add(suffix.get(0));
            } else {
                // Get item from list with random on equal probability
                output.add(suffix.get(r.nextInt(suffix.size())));
            }

            // Reach L size of output words
            if (output.size() >= outputSize) {
                return String.join("",
                        output.stream()
                        .limit(outputSize)
                        .collect(Collectors.joining(" "))
                        .split("(,\\.|\\s+)(?=[,.])"));
            }

            n++;

            prefix = new StringBuilder(output.stream()
                    .skip(n)
                    .limit(keySize)
                    .reduce("", (a, b) -> a + " " + b).trim());
        }
    }

    static String[] readFile(String filePath) {
        Path path = Paths.get(filePath);
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new Error(e);
        }

        return new String(bytes).trim()
                .toLowerCase()
                .split("(((?=[,.])|\\s+))");
}

    private static void writeFile(String filePath, String fileContent) {
        Path path = Paths.get(filePath);

        try {
            Files.write(path, fileContent.getBytes());
        } catch (IOException e) {
            throw new Error(e);
        }

    }

    private static StringBuilder getRandomKeyByDistribution(int keySize, String[] words, StringBuilder prefix) {
        prefix.setLength(0);
        prefix.append(getOnWeight(words));

        for (int i = 0; i < keySize - 1; i++) {
            String w = getOnWeight(words);

            if (w.equals(".") || w.equals(",")) {
                prefix.append(w);
            } else {
                prefix.append(' ').append(w);
            }

        }

        return prefix;
    }

    static String getOnWeight(String[] words) {
        double rn = r.nextDouble();
        double countWeight = 0.0;

        for (String word: getUniqueWords(words)) {
            if (word.equals("")) {
                continue;
            }

            countWeight = countWeight + search(words, word);
            if (countWeight >= rn) {
                return word;
            }
        }
        throw new Error("Random element not found.");
    }

    /**
     * Get probability of item on the array
     *
     * @param words
     * @param s
     * @return
     */
    static double search(String[] words, String s) {
        int counter = 0;
        int zeroCount = 0;
        for (String word : words) {
            if (word.equals("")) {
                zeroCount++;
                continue;
            }
            if (s.equals(word)) {
                counter++;
            }
        }

        return (double) counter / (double) (words.length - zeroCount);
    }

    static String[] getUniqueWords(String[] words) {
        return Arrays.stream(words).distinct().toArray(String[]::new);
    }

}

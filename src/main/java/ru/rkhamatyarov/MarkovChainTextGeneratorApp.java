package ru.rkhamatyarov;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MarkovChainTextGeneratorApp {
    private static Random r = new Random();

    public static void main(String[] args) throws IOException {
        String inputFileName = args[0];
        String outputFileName = args[1];

        final int N = Integer.parseInt(args[2]);
        checkRangeOfN(N);

        final long L = Long.parseLong(args[3]);
        checkRangeOfL(L);

        writeFile(outputFileName, markov(inputFileName, N, L));
        System.out.println("Text wrote successfully in the file: " + outputFileName);
    }

    public static void checkRangeOfN(int N) {
        if (N <= 0 || N >= 20) {
            throw new Error("N out of range (0, 20)");
        }
    }

    public static void checkRangeOfL(long L) {
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
            StringBuilder key = new StringBuilder(words[i]);
            for (int j = i + 1; j < i + keySize; ++j) {
                key.append(' ').append(words[j]);
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

        StringBuilder prefix = getRandomKeyByDistribution(keySize, words, new StringBuilder());

        List<String> output = new ArrayList<>(Arrays.asList(prefix.toString().split(" ")));

        while (true) {
            if (!dict.containsKey(prefix.toString())) {
                //System.out.println(prefix );
                prefix = getRandomKeyByDistribution(keySize, words, prefix);
                continue;
            }


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
                return String.join("", output.stream()
                        .limit(outputSize)
                        .collect(Collectors.joining(" "))
                        .split(" \\. | ,"));
            }

            n++;

            prefix = new StringBuilder(output.stream()
                    .skip(n)
                    .limit(keySize)
                    .reduce("", (a, b) -> a + " " + b).trim());
        }
    }

    private static String[] readFile(String filePath) {
        Path path = Paths.get(filePath);
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new Error(e);
        }

        return Arrays.stream(new String(bytes).trim()
                .toLowerCase()
                .split("(((?=[,.])|\\s+))"))
                .filter(s -> !s.equals(""))
                .toArray(String[]::new);
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
            prefix.append(' ').append(getOnWeight(words));
        }
        return prefix;
    }

    private static String getOnWeight(String[] words) {
        double rn = r.nextDouble();
        double countWeight = 0.0;

        for (String w : getUniqueWords(words)) {
            countWeight = countWeight + search(words, w);
            if (countWeight >= rn) {
                return w;
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
    private static double search(String[] words, String s) {
        int counter = 0;
        for (String word : words) {
            if (s.equals(word)) {
                counter++;
            }
        }

        return (double) counter / (double) words.length;
    }

    private static String[] getUniqueWords(String[] words) {
        return Arrays.stream(words).distinct().toArray(String[]::new);
    }

    private static void checkProbabilityDistribution(String[] words) {
        String[] l = new String[10000];
        for (int i = 0; i < 10000; i++) {
            l[i] = getOnWeight(words);
        }
        for (String w : words) {
            System.out.println(w + " " + search(l, w));
        }
    }

}

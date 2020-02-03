package ru.rkhamatyarov;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static ru.rkhamatyarov.MarkovChainTextGeneratorApp.*;

class MarkovChainTextGeneratorAppTest {
    private static final Logger log = LoggerFactory.getLogger(MarkovChainTextGeneratorAppTest.class);


    @Test
    void getOnWeightTest() {
        String[] words = readFile("in.txt");

        String[] l = new String[10000];
        for (int i = 0; i < 10000; i++) {
            l[i] = getOnWeight(words);
        }

        double sum = 0.0;
        for (String w : getUniqueWords(words)) {
            double p = search(l, w);
            log.debug("Probability distribution {}      {}", w, p);
            sum += p;
        }

        log.debug("Probability distribution sum {}", sum);
        assertEquals((int) Math.round(sum), 1);
    }

    @Test
    void searchTest() {
        String[] words = readFile("in.txt");

        for (String w : getUniqueWords(words)) {
             double p = search(words, w);
             assertTrue(p < 1);
        }
    }

    @Test
    void readFileTest() {
        String[] words = readFile("in.txt");

        assertNotNull(words);
        assertTrue(words.length > 0);
    }
}
package ru.rkhamatyarov.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.rkhamatyarov.entity.RusMarkovChain.rusMarkovChain;

class RusMarkovChainTest {

    @Test
    void fromFileTest() {
        String generatedText = rusMarkovChain().fromFile("in.txt", 2).generateText(1);
        assertNotNull(generatedText, "Generated text is not null.");
        assertEquals(generatedText.split("[\\p{Punct}\\s]+").length, 2, "Generated text length is equals.");
    }
}
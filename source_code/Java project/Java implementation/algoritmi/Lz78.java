package algoritmi;

import podaci.Lz78Token;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lz78 {
    public static List<Lz78Token> compress(byte[] input) {
        Map<String, Integer> dictionary = new HashMap<>();
        List<Lz78Token> result = new ArrayList<>();
        int nextIndex = 1;
        String current = "";
        String text = new String(input, StandardCharsets.ISO_8859_1);

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            String candidate = current + character;

            if (dictionary.containsKey(candidate)) {
                current = candidate;
            } else {
                int prefixIndex = dictionary.getOrDefault(current, 0);
                result.add(new Lz78Token(prefixIndex, character));
                dictionary.put(candidate, nextIndex++);
                current = "";
            }
        }

        if (!current.isEmpty()) {
            result.add(new Lz78Token(dictionary.get(current), 256));
        }

        return result;
    }

    public static byte[] decompress(List<Lz78Token> input) {
        Map<Integer, String> dictionary = new HashMap<>();
        StringBuilder result = new StringBuilder();
        int nextIndex = 1;
        dictionary.put(0, "");

        for (Lz78Token token : input) {
            String phrase = dictionary.get(token.prefixIndex);
            if (phrase == null) {
                throw new IllegalArgumentException("Neispravan LZ78 prefiks: " + token.prefixIndex);
            }

            if (token.nextByte != 256) {
                phrase = phrase + (char) token.nextByte;
            }

            result.append(phrase);
            dictionary.put(nextIndex++, phrase);
        }

        return result.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}

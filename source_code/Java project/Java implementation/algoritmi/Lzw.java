package algoritmi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lzw {
    private static final int MAX_DICTIONARY_SIZE = 4096;

    public static List<Integer> compress(byte[] input) {
        Map<String, Integer> dictionary = new HashMap<>();
        List<Integer> result = new ArrayList<>();
        String text = new String(input, StandardCharsets.ISO_8859_1);

        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        String current = "";
        int nextCode = 256;

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            String candidate = current + character;

            if (dictionary.containsKey(candidate)) {
                current = candidate;
            } else {
                result.add(dictionary.get(current));
                if (nextCode < MAX_DICTIONARY_SIZE) {
                    dictionary.put(candidate, nextCode++);
                }
                current = String.valueOf(character);
            }
        }

        if (!current.isEmpty()) {
            result.add(dictionary.get(current));
        }

        return result;
    }

    public static byte[] decompress(List<Integer> input) {
        if (input.isEmpty()) {
            return new byte[0];
        }

        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        int nextCode = 256;
        String previous = dictionary.get(input.get(0));
        StringBuilder result = new StringBuilder(previous);

        for (int i = 1; i < input.size(); i++) {
            int code = input.get(i);
            String entry;

            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            } else if (code == nextCode) {
                entry = previous + previous.charAt(0);
            } else {
                throw new IllegalArgumentException("Neispravan LZW kod: " + code);
            }

            result.append(entry);
            if (nextCode < MAX_DICTIONARY_SIZE) {
                dictionary.put(nextCode++, previous + entry.charAt(0));
            }
            previous = entry;
        }

        return result.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}

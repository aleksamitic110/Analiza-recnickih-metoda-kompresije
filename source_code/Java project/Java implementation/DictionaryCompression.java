import izvrsavanje.CompressionRunner;

import java.io.IOException;
import java.nio.file.Path;

public class DictionaryCompression {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Upotreba:");
            System.out.println("  javac -d build (Get-ChildItem -Path \"Java implementation\" -Recurse -Filter *.java).FullName");
            System.out.println("  java -cp build DictionaryCompression lz78 primeri\\tekst_ponavljanja.txt rezultati");
            System.out.println("  java -cp build DictionaryCompression lzw primeri\\tekst_ponavljanja.txt rezultati");
            return;
        }

        String algorithm = args[0].toLowerCase();
        Path inputPath = Path.of(args[1]);
        Path outputDir = Path.of(args[2]);

        if ("lz78".equals(algorithm)) {
            CompressionRunner.runLz78(inputPath, outputDir);
        } else if ("lzw".equals(algorithm)) {
            CompressionRunner.runLzw(inputPath, outputDir);
        } else {
            throw new IllegalArgumentException("Algoritam mora biti lz78 ili lzw.");
        }
    }
}

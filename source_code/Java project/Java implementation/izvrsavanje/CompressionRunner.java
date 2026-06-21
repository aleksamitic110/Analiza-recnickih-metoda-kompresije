package izvrsavanje;

import algoritmi.Lz78;
import algoritmi.Lzw;
import interfejs.OutputPrinter;
import podaci.FileNameUtils;
import podaci.CompressedFileFormat;
import podaci.CompressedFileFormat.CompressedPayload;
import podaci.Lz78Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompressionRunner {
    public static void compressToFile(String algorithm, Path inputPath, Path outputPath) throws IOException {
        byte[] input = Files.readAllBytes(inputPath);

        if ("lz78".equalsIgnoreCase(algorithm)) {
            List<Lz78Token> compressed = Lz78.compress(input);
            CompressedFileFormat.writeLz78(outputPath, input.length, compressed);
        } else if ("lzw".equalsIgnoreCase(algorithm)) {
            List<Integer> compressed = Lzw.compress(input);
            CompressedFileFormat.writeLzw(outputPath, input.length, compressed);
        } else {
            throw new IllegalArgumentException("Algoritam mora biti lz78 ili lzw.");
        }
    }

    public static void decompressFromFile(Path inputPath, Path outputPath) throws IOException {
        CompressedPayload payload = CompressedFileFormat.read(inputPath);
        byte[] decompressed;

        if (payload.algorithm == CompressedFileFormat.ALG_LZ78) {
            decompressed = Lz78.decompress(payload.lz78Tokens);
        } else if (payload.algorithm == CompressedFileFormat.ALG_LZW) {
            decompressed = Lzw.decompress(payload.lzwCodes);
        } else {
            throw new IllegalArgumentException("Nepoznat algoritam u fajlu.");
        }

        if (decompressed.length != payload.originalSize) {
            throw new IOException("Dekomprimovana duzina se ne poklapa sa zaglavljem.");
        }

        Files.createDirectories(outputPath.toAbsolutePath().getParent());
        Files.write(outputPath, decompressed);
    }

    public static void runLz78(Path inputPath, Path outputDir) throws IOException {
        byte[] input = Files.readAllBytes(inputPath);
        long start = System.nanoTime();
        List<Lz78Token> compressed = Lz78.compress(input);
        byte[] decompressed = Lz78.decompress(compressed);
        long duration = System.nanoTime() - start;

        String baseName = FileNameUtils.removeExtension(inputPath.getFileName().toString());
        Path codesPath = outputDir.resolve(baseName + "_lz78_java_kodovi.txt");
        Path decompressedPath = outputDir.resolve(baseName + "_lz78_java_dekompresovan.bin");

        List<String> lines = new ArrayList<>();
        for (Lz78Token token : compressed) {
            lines.add(token.prefixIndex + "," + token.nextByte);
        }

        Files.createDirectories(outputDir);
        Files.write(codesPath, lines, StandardCharsets.UTF_8);
        Files.write(decompressedPath, decompressed);

        OutputPrinter.printStats(
            "LZ78",
            input,
            compressed.size() * 4,
            compressed.size(),
            duration,
            codesPath,
            decompressedPath
        );
    }

    public static void runLzw(Path inputPath, Path outputDir) throws IOException {
        byte[] input = Files.readAllBytes(inputPath);
        long start = System.nanoTime();
        List<Integer> compressed = Lzw.compress(input);
        byte[] decompressed = Lzw.decompress(compressed);
        long duration = System.nanoTime() - start;

        String baseName = FileNameUtils.removeExtension(inputPath.getFileName().toString());
        Path codesPath = outputDir.resolve(baseName + "_lzw_java_kodovi.txt");
        Path decompressedPath = outputDir.resolve(baseName + "_lzw_java_dekompresovan.bin");

        List<String> lines = new ArrayList<>();
        for (Integer code : compressed) {
            lines.add(code.toString());
        }

        Files.createDirectories(outputDir);
        Files.write(codesPath, lines, StandardCharsets.UTF_8);
        Files.write(decompressedPath, decompressed);

        int packedSize = (int) Math.ceil(compressed.size() * 12 / 8.0);
        OutputPrinter.printStats(
            "LZW",
            input,
            packedSize,
            compressed.size(),
            duration,
            codesPath,
            decompressedPath
        );
    }
}

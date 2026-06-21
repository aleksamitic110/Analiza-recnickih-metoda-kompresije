package podaci;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompressedFileFormat {
    public static final int ALG_LZ78 = 1;
    public static final int ALG_LZW = 2;

    private static final byte[] MAGIC = new byte[] {'R', 'K', 'C', 'J', '2', '6'};
    private static final int VERSION = 1;

    public static void writeLz78(Path outputPath, long originalSize, List<Lz78Token> tokens) throws IOException {
        Files.createDirectories(outputPath.toAbsolutePath().getParent());
        try (DataOutputStream output = new DataOutputStream(
            new BufferedOutputStream(Files.newOutputStream(outputPath))
        )) {
            writeHeader(output, ALG_LZ78, originalSize, tokens.size());
            for (Lz78Token token : tokens) {
                output.writeInt(token.prefixIndex);
                output.writeInt(token.nextByte);
            }
        }
    }

    public static void writeLzw(Path outputPath, long originalSize, List<Integer> codes) throws IOException {
        Files.createDirectories(outputPath.toAbsolutePath().getParent());
        try (DataOutputStream output = new DataOutputStream(
            new BufferedOutputStream(Files.newOutputStream(outputPath))
        )) {
            writeHeader(output, ALG_LZW, originalSize, codes.size());
            for (Integer code : codes) {
                output.writeInt(code);
            }
        }
    }

    public static CompressedPayload read(Path inputPath) throws IOException {
        try (DataInputStream input = new DataInputStream(
            new BufferedInputStream(Files.newInputStream(inputPath))
        )) {
            byte[] magic = input.readNBytes(MAGIC.length);
            if (!Arrays.equals(magic, MAGIC)) {
                throw new IOException("Nepoznat format fajla.");
            }

            int version = input.readUnsignedByte();
            if (version != VERSION) {
                throw new IOException("Nepodrzana verzija formata: " + version);
            }

            int algorithm = input.readUnsignedByte();
            long originalSize = input.readLong();
            int itemCount = input.readInt();
            if (originalSize < 0 || itemCount < 0) {
                throw new IOException("Neispravno zaglavlje fajla.");
            }

            if (algorithm == ALG_LZ78) {
                List<Lz78Token> tokens = new ArrayList<>();
                for (int i = 0; i < itemCount; i++) {
                    tokens.add(new Lz78Token(input.readInt(), input.readInt()));
                }
                return CompressedPayload.forLz78(originalSize, tokens);
            }

            if (algorithm == ALG_LZW) {
                List<Integer> codes = new ArrayList<>();
                for (int i = 0; i < itemCount; i++) {
                    codes.add(input.readInt());
                }
                return CompressedPayload.forLzw(originalSize, codes);
            }

            throw new IOException("Nepoznat algoritam u fajlu.");
        } catch (EOFException exc) {
            throw new IOException("Fajl je ostecen ili nepotpun.", exc);
        }
    }

    private static void writeHeader(
        DataOutputStream output,
        int algorithm,
        long originalSize,
        int itemCount
    ) throws IOException {
        output.write(MAGIC);
        output.writeByte(VERSION);
        output.writeByte(algorithm);
        output.writeLong(originalSize);
        output.writeInt(itemCount);
    }

    public static class CompressedPayload {
        public final int algorithm;
        public final long originalSize;
        public final List<Lz78Token> lz78Tokens;
        public final List<Integer> lzwCodes;

        private CompressedPayload(
            int algorithm,
            long originalSize,
            List<Lz78Token> lz78Tokens,
            List<Integer> lzwCodes
        ) {
            this.algorithm = algorithm;
            this.originalSize = originalSize;
            this.lz78Tokens = lz78Tokens;
            this.lzwCodes = lzwCodes;
        }

        public static CompressedPayload forLz78(long originalSize, List<Lz78Token> tokens) {
            return new CompressedPayload(ALG_LZ78, originalSize, tokens, List.of());
        }

        public static CompressedPayload forLzw(long originalSize, List<Integer> codes) {
            return new CompressedPayload(ALG_LZW, originalSize, List.of(), codes);
        }
    }
}

package interfejs;

import java.nio.file.Path;

public class OutputPrinter {
    public static void printStats(
        String algorithm,
        byte[] original,
        int estimatedCompressedSize,
        int codeCount,
        long duration,
        Path codesPath,
        Path decompressedPath
    ) {
        double ratio = original.length == 0 ? 0 : (double) estimatedCompressedSize / original.length;
        System.out.println("Algoritam: " + algorithm);
        System.out.println("Velicina originala: " + original.length + " B");
        System.out.println("Broj kodova/tokena: " + codeCount);
        System.out.println("Procenjena kompresovana velicina: " + estimatedCompressedSize + " B");
        System.out.printf("Kompresioni odnos: %.4f%n", ratio);
        System.out.printf("Vreme kompresije i dekompresije: %.3f ms%n", duration / 1_000_000.0);
        System.out.println("Kodovi su upisani u: " + codesPath);
        System.out.println("Dekomprimovan fajl je upisan u: " + decompressedPath);
    }
}

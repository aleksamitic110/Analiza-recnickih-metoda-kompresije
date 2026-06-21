package podaci;

public class Lz78Token {
    public final int prefixIndex;
    public final int nextByte;

    public Lz78Token(int prefixIndex, int nextByte) {
        this.prefixIndex = prefixIndex;
        this.nextByte = nextByte;
    }
}

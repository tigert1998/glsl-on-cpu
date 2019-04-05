package ast.types;

public class UvecnType extends Type {
    private int n;

    private static UvecnType[] predefinedTypes = new UvecnType[3];

    static {
        for (int i = 2; i <= 4; i++) predefinedTypes[i - 2] = new UvecnType(i);
    }

    static public UvecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    public int getN() {
        return n;
    }

    private UvecnType(int n) { this.n = n; }

    static public UvecnType fromText(String text) {
        int digit = text.charAt(text.length() - 1) - '0';
        return fromN(digit);
    }

    @Override
    public boolean equals(Object obj) {
        if (UvecnType.class != obj.getClass()) return false;
        return ((UvecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "uvec" + n;
    }

    @Override
    public Type collapse() {
        return UintType.TYPE;
    }
}

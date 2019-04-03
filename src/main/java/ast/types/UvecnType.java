package ast.types;

public class UvecnType extends Type {
    private int n;

    private static UvecnType[] predefinedTypes = new UvecnType[5];

    static {
        for (int i = 2; i <= 4; i++) predefinedTypes[i] = new UvecnType(i);
    }

    static public UvecnType fromN(int n) {
        return predefinedTypes[n];
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
}

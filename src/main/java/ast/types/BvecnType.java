package ast.types;

public class BvecnType extends Type {
    private int n;

    // prevent multiple new
    private static BvecnType[] predefinedTypes = new BvecnType[3];

    static {
        for (int i = 2; i <= 4; i++) predefinedTypes[i - 2] = new BvecnType(i);
    }

    static public BvecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    public int getN() {
        return n;
    }

    static public BvecnType fromText(String text) {
        int digit = text.charAt(text.length() - 1) - '0';
        return fromN(digit);
    }

    private BvecnType(int n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        if (BvecnType.class != obj.getClass()) return false;
        return ((BvecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "bvec" + n;
    }

    public Type collapse() {
        return BoolType.TYPE;
    }
}

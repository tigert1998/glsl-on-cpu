package ast.types;

public class IvecnType extends Type {
    private int n;

    // prevent multiple new
    private static IvecnType[] predefinedTypes = new IvecnType[5];

    static {
        for (int i = 2; i <= 4; i++) predefinedTypes[i] = new IvecnType(i);
    }

    private IvecnType(int n) {
        this.n = n;
    }

    static public IvecnType fromN(int n) {
        return predefinedTypes[n];
    }

    static public IvecnType fromText(String text) {
        int digit = text.charAt(text.length() - 1);
        return fromN(digit);
    }

    @Override
    public boolean equals(Object obj) {
        if (IvecnType.class != obj.getClass()) return false;
        return ((IvecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "ivec" + n;
    }
}

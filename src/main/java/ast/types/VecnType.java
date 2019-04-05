package ast.types;

public class VecnType extends Type {
    private int n;

    private static VecnType[] predefinedTypes = new VecnType[3];

    static {
        for (int i = 2; i <= 4; i++) predefinedTypes[i - 2] = new VecnType(i);
    }

    static public VecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    public int getN() {
        return n;
    }

    private VecnType(int n) {
        this.n = n;
    }

    static public VecnType fromText(String text) {
        int digit = text.charAt(text.length() - 1) - '0';
        return fromN(digit);
    }

    @Override
    public boolean equals(Object obj) {
        if (VecnType.class != obj.getClass()) return false;
        return ((VecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "vec" + n;
    }

    @Override
    public Type collapse() {
        return FloatType.TYPE;
    }
}

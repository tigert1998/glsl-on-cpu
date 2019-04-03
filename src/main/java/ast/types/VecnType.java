package ast.types;

public class VecnType extends Type {
    private int n;

    private static VecnType[] predefinedTypes = new VecnType[5];

    static {
        for (int i = 2; i <= 4; i++) predefinedTypes[i] = new VecnType(i);
    }

    static public VecnType fromN(int n) {
        return predefinedTypes[n];
    }

    public VecnType(int n) {
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
}

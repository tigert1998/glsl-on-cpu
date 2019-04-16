package ast.types;

import ast.values.*;

public class UvecnType extends Type {
    private int n;

    private static UvecnType[] predefinedTypes = new UvecnType[3];
    private static UvecnValue[] defaultValues = new UvecnValue[3];

    static {
        UintValue zeroValue = UintType.TYPE.getDefaultValue();
        for (int i = 2; i <= 4; i++) {
            predefinedTypes[i - 2] = new UvecnType(i);
            defaultValues[i - 2] = new UvecnValue(predefinedTypes[i - 2], zeroValue);
        }
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

    @Override
    public UvecnValue getDefaultValue() {
        return defaultValues[n - 2];
    }
}

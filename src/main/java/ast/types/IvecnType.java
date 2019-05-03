package ast.types;

import ast.values.*;

public class IvecnType extends Type implements SwizzledType {
    private int n;

    // prevent multiple new
    private static IvecnType[] predefinedTypes = new IvecnType[3];
    private static IvecnValue[] defaultValues = new IvecnValue[3];

    static {
        IntValue zeroValue = IntType.TYPE.getDefaultValue();
        for (int i = 2; i <= 4; i++) {
            predefinedTypes[i - 2] = new IvecnType(i);
            defaultValues[i - 2] = new IvecnValue(predefinedTypes[i - 2], zeroValue);
        }
    }

    @Override
    public Type elementType() {
        return IntType.TYPE;
    }

    private IvecnType(int n) {
        this.n = n;
    }

    static public IvecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public SwizzledType changeN(int n) {
        return fromN(n);
    }

    static public IvecnType fromText(String text) {
        int digit = text.charAt(text.length() - 1) - '0';
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

    @Override
    public IvecnValue getDefaultValue() {
        return defaultValues[n - 2];
    }
}

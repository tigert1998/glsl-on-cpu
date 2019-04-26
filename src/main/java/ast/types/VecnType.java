package ast.types;

import ast.values.*;

public class VecnType extends Type implements SwizzleType {
    private int n;

    private static VecnType[] predefinedTypes = new VecnType[3];
    private static VecnValue[] defaultValues = new VecnValue[3];

    static {
        FloatValue zeroValue = FloatType.TYPE.getDefaultValue();
        for (int i = 2; i <= 4; i++) {
            predefinedTypes[i - 2] = new VecnType(i);
            defaultValues[i - 2] = new VecnValue(predefinedTypes[i - 2], zeroValue);
        }
    }

    static public VecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public SwizzleType changeN(int n) {
        return fromN(n);
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

    @Override
    public VecnValue getDefaultValue() {
        return defaultValues[n - 2];
    }
}

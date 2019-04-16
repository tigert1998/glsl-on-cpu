package ast.types;

import ast.values.IntValue;
import ast.values.IvecnValue;
import ast.values.Value;

public class IvecnType extends Type {
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

    private IvecnType(int n) {
        this.n = n;
    }

    static public IvecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    public int getN() {
        return n;
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
    public Type collapse() {
        return IntType.TYPE;
    }

    @Override
    public IvecnValue getDefaultValue() {
        return defaultValues[n - 2];
    }
}

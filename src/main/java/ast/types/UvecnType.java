package ast.types;

import ast.exceptions.*;
import ast.values.*;

import java.util.*;

public class UvecnType extends Type implements SwizzledType, IncreasableType {
    private int n;

    private static UvecnType[] predefinedTypes = new UvecnType[3];
    private static UvecnValue[] zeros = new UvecnValue[3];
    private static UvecnValue[] ones = new UvecnValue[3];

    static {
        for (int i = 2; i <= 4; i++) {
            predefinedTypes[i - 2] = new UvecnType(i);
            zeros[i - 2] = new UvecnValue(predefinedTypes[i - 2], UintType.TYPE.zero());
            ones[i - 2] = new UvecnValue(predefinedTypes[i - 2], UintType.TYPE.one());
        }
    }

    static public UvecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    @Override
    public Type elementType() {
        return UintType.TYPE;
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public SwizzledType changeN(int n) {
        return fromN(n);
    }

    private UvecnType(int n) {
        this.n = n;
    }

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
    public UvecnValue zero() {
        return zeros[n - 2];
    }

    @Override
    public UvecnValue one() {
        return ones[n - 2];
    }

    @Override
    public UvecnValue construct(Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new UvecnValue(this, UintType.TYPE.construct(values));
        }
        List<UintValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(UintType.TYPE.construct(new Value[]{newValue}));
            } else {
                valueList.add(UintType.TYPE.construct(new Value[]{value}));
            }
        }
        if (valueList.size() < this.getN())
            throw ConstructionFailedException.notEnoughData();
        return new UvecnValue(this, valueList);
    }
}

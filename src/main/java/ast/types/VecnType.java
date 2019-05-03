package ast.types;

import ast.exceptions.*;
import ast.values.*;

import java.util.*;

public class VecnType extends Type implements SwizzledType, IncreasableType {
    private int n;

    private static VecnType[] predefinedTypes = new VecnType[3];
    private static VecnValue[] zeros = new VecnValue[3];
    private static VecnValue[] ones = new VecnValue[3];

    static {
        for (int i = 2; i <= 4; i++) {
            predefinedTypes[i - 2] = new VecnType(i);
            zeros[i - 2] = new VecnValue(predefinedTypes[i - 2], FloatType.TYPE.zero());
            ones[i - 2] = new VecnValue(predefinedTypes[i - 2], FloatType.TYPE.one());
        }
    }

    static public VecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    @Override
    public Type elementType() {
        return FloatType.TYPE;
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public SwizzledType changeN(int n) {
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
    public VecnValue zero() {
        return zeros[n - 2];
    }

    @Override
    public Value one() {
        return ones[n - 2];
    }

    @Override
    public VecnValue construct(Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new VecnValue(this, FloatType.TYPE.construct(values));
        }
        List<FloatValue> valueList = flattenThenConvertToFloatValue(values);
        if (valueList.size() < this.getN())
            throw ConstructionFailedException.notEnoughData();
        return new VecnValue(this, valueList);
    }
}

package ast.types;

import ast.exceptions.*;
import ast.values.*;

public class FloatType extends Type implements IncreasableType {
    public static FloatType TYPE = new FloatType();

    private static FloatValue zero = new FloatValue(0);
    private static FloatValue one = new FloatValue(1);

    @Override
    public boolean equals(Object obj) {
        return FloatType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "float";
    }

    @Override
    public FloatValue zero() {
        return zero;
    }

    @Override
    public FloatValue construct(Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return new FloatValue(((UintValue) value).value);
        } else if (value instanceof BoolValue) {
            return new FloatValue(((BoolValue) value).value ? 1 : 0);
        } else if (value instanceof FloatValue) {
            return (FloatValue) value;
        } else if (value instanceof IntValue) {
            return new FloatValue(((IntValue) value).value);
        } else if (value instanceof Vectorized) {
            return FloatType.TYPE.construct(new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), FloatType.TYPE);
    }

    @Override
    public FloatValue one() {
        return one;
    }
}

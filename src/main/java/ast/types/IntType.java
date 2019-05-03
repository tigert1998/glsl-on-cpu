package ast.types;

import ast.exceptions.ConstructionFailedException;
import ast.values.*;

public class IntType extends Type {
    public static IntType TYPE = new IntType();

    private static IntValue defaultValue = new IntValue(0);

    @Override
    public boolean equals(Object obj) {
        return IntType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "int";
    }

    @Override
    public IntValue getDefaultValue() {
        return defaultValue;
    }

    @Override
    public IntValue construct(Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return new IntValue((int) (long) ((UintValue) value).value);
        } else if (value instanceof BoolValue) {
            return new IntValue(((BoolValue) value).value ? 1 : 0);
        } else if (value instanceof FloatValue) {
            return new IntValue(dropFractionPart(((FloatValue) value).value));
        } else if (value instanceof IntValue) {
            return (IntValue) value;
        } else if (value instanceof Vectorized) {
            return IntType.TYPE.construct(new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), IntType.TYPE);

    }
}

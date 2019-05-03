package ast.types;

import ast.values.*;

public class FloatType extends Type {
    public static FloatType TYPE = new FloatType();

    private static FloatValue defaultValue = new FloatValue(0);

    @Override
    public boolean equals(Object obj) {
        return FloatType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "float";
    }

    @Override
    public FloatValue getDefaultValue() {
        return defaultValue;
    }
}

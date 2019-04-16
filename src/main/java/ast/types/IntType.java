package ast.types;

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
    public Type collapse() {
        return TYPE;
    }

    @Override
    public IntValue getDefaultValue() {
        return defaultValue;
    }
}

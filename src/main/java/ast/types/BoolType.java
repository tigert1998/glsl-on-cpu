package ast.types;

import ast.values.*;

public class BoolType extends Type {
    public static BoolType TYPE = new BoolType();

    private static BoolValue defaultValue = new BoolValue(false);

    @Override
    public boolean equals(Object obj) {
        return BoolType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "bool";
    }

    @Override
    public BoolValue getDefaultValue() {
        return defaultValue;
    }
}

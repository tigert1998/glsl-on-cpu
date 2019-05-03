package ast.types;

import ast.values.*;

public class UintType extends Type {
    public static UintType TYPE = new UintType();

    private static UintValue defaultValue = new UintValue(0);

    @Override
    public boolean equals(Object obj) {
        return UintType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "uint";
    }

    @Override
    public UintValue getDefaultValue() {
        return defaultValue;
    }
}

package ast.types;

import ast.values.Value;

public class VoidType extends Type {
    public static Type TYPE = new VoidType();

    @Override
    public Type collapse() {
        return this;
    }

    @Override
    public Value getDefaultValue() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }

    @Override
    public String toString() {
        return "void";
    }
}

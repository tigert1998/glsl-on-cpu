package ast.types;

import ast.exceptions.ConstructionFailedException;
import ast.values.Value;

public class VoidType extends Type {
    public static Type TYPE = new VoidType();

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

    @Override
    public Value construct(Value[] values) throws ConstructionFailedException {
        throw ConstructionFailedException.constructVoid();
    }
}

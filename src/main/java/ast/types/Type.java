package ast.types;

import ast.values.Value;

public abstract class Type {
    abstract public Type collapse();

    abstract public Value getDefaultValue();
}

package ast.types;

import ast.values.Value;

public abstract class Type {
    abstract public Value getDefaultValue();
}

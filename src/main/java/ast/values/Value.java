package ast.values;

import ast.types.*;

public abstract class Value {
    protected Type type;

    public Type getType() {
        return type;
    }
}

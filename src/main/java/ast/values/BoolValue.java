package ast.values;

import ast.types.*;

public class BoolValue extends Value {
    public Boolean value = null;

    public BoolValue(boolean value) {
        this.value = value;
        this.type = BoolType.TYPE;
    }

    @Override
    public String toString() {
        return value + ": bool";
    }
}

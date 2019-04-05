package ast.values;

import ast.types.*;

public class ArrayValue extends Value {
    public Value[] values = null;

    public ArrayValue(ArrayType type, Value[] values) {
        this.values = values;
        this.type = type;
    }
}

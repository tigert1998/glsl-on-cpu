package ast.values;

import ast.types.*;

public class IntValue extends Value {
    public Integer value = null;

    public IntValue(int value) {
        this.value = value;
        this.type = IntType.TYPE;
    }
}

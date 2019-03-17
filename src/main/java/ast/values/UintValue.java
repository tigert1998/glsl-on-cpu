package ast.values;

import ast.types.*;

public class UintValue extends Value {
    public Long value = null;

    public UintValue(long value) {
        this.value = value;
        this.type = UintType.TYPE;
    }
}

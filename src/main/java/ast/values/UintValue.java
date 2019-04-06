package ast.values;

import ast.types.*;

public class UintValue extends Value {
    public long value;

    public UintValue(long value) {
        this.value = value;
        this.type = UintType.TYPE;
    }

    @Override
    public String toString() {
        return value + "u";
    }
}

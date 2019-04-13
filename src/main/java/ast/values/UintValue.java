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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UintValue)) return false;
        var uint = (UintValue) obj;
        return uint.value == this.value;
    }
}

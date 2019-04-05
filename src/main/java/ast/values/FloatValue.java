package ast.values;

import ast.types.*;

public class FloatValue extends Value {
    public float value;

    public FloatValue(float value) {
        this.value = value;
        this.type = FloatType.TYPE;
    }

    @Override
    public String toString() {
        return value + ": float";
    }
}

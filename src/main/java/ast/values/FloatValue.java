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
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloatValue)) return false;
        var bool = (FloatValue) obj;
        return bool.value == this.value;
    }
}

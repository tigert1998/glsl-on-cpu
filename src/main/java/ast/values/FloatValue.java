package ast.values;

import ast.types.*;

public class FloatValue extends Value {
    public Float value = null;

    public FloatValue(float value) {
        this.value = value;
        this.type = FloatType.TYPE;
    }
}

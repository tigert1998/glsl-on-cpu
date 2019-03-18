package ast.values;

import ast.types.*;

import java.util.function.*;

public class VecnValue extends Value {
    public Float[] value = null;

    public VecnValue(int n) {
        this.type = VecnType.fromN(n);
        value = new Float[n];
    }

    public VecnValue map(Function<Float, Float> f) {
        VecnValue result = new VecnValue(value.length);
        for (int i = 0; i < value.length; i++)
            result.value[i] = f.apply(value[i]);
        return result;
    }
}

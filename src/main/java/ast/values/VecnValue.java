package ast.values;

import java.util.function.*;

public class VecnValue extends Value {
    public Float[] value = null;

    public void map(Function<Float, Float> f) {
        for (int i = 0; i < value.length; i++)
            value[i] = f.apply(value[i]);
    }
}

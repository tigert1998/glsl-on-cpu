package ast.values;

import java.util.function.*;

public class IvecnValue extends Value {
    public Integer[] value = null;

    public void map(Function<Integer, Integer> f) {
        for (int i = 0; i < value.length; i++)
            value[i] = f.apply(value[i]);
    }
}

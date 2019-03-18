package ast.values;

import ast.types.*;

import java.util.function.*;

public class IvecnValue extends Value {
    public Integer[] value = null;

    public IvecnValue(int n) {
        this.type = IvecnType.fromN(n);
        value = new Integer[n];
    }

    public IvecnValue map(Function<Integer, Integer> f) {
        IvecnValue result = new IvecnValue(value.length);
        for (int i = 0; i < value.length; i++)
            result.value[i] = f.apply(value[i]);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ivec" + value.length + "(" + value[0]);
        for (int i = 1; i < value.length; i++) builder.append(", ").append(value[i]);
        builder.append(")");
        return new String(builder);
    }
}

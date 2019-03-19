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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("vec" + value.length + "(" + value[0]);
        for (int i = 1; i < value.length; i++) builder.append(", ").append(value[i]);
        builder.append(")");
        return new String(builder);
    }

    static public VecnValue applyFunction(VecnValue x, VecnValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(x.value.length);
        for (int i = 0; i < res.value.length; i++)
            res.value[i] = f.apply(x.value[i], y.value[i]);
        return res;
    }

    static public VecnValue applyFunction(VecnValue x, FloatValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(x.value.length);
        for (int i = 0; i < res.value.length; i++)
            res.value[i] = f.apply(x.value[i], y.value);
        return res;
    }
}

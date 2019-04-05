package ast.values;

import ast.types.*;

import java.util.function.*;

public class VecnValue extends Value {
    public Float[] value = null;

    public VecnValue(int n) {
        this.type = VecnType.fromN(n);
        value = new Float[n];
    }

    public int getN() {
        return value.length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("vec" + value.length + "(" + value[0]);
        for (int i = 1; i < value.length; i++) builder.append(", ").append(value[i]);
        builder.append(")");
        return new String(builder);
    }

    static public VecnValue pointwise(VecnValue x, Function<Float, Float> f) {
        VecnValue result = new VecnValue(x.getN());
        for (int i = 0; i < x.getN(); i++)
            result.value[i] = f.apply(x.value[i]);
        return result;
    }

    static public VecnValue pointwise(VecnValue x, VecnValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value[i], y.value[i]);
        return res;
    }

    static public VecnValue pointwise(VecnValue x, FloatValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value[i], y.value);
        return res;
    }

    static public VecnValue pointwise(FloatValue x, VecnValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(y.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value, y.value[i]);
        return res;
    }
}

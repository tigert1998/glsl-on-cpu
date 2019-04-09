package ast.values;

import ast.exceptions.*;
import ast.types.*;

import java.util.*;
import java.util.function.*;

public class VecnValue extends Value implements Vectorized, Indexed {
    public float[] values = null;

    public VecnValue(int n) {
        this.type = VecnType.fromN(n);
        values = new float[n];
    }

    public VecnValue(VecnType type, FloatValue v) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++) values[i] = v.value;
    }

    public VecnValue(VecnType type, List<FloatValue> values) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++)
            this.values[i] = values.get(i).value;
    }

    public int getN() {
        return values.length;
    }

    @Override
    public Value[] retrieve() {
        var result = new Value[getN()];
        for (int i = 0; i < result.length; i++)
            result[i] = new FloatValue(values[i]);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("vec" + values.length + "(" + values[0]);
        for (int i = 1; i < values.length; i++) builder.append(", ").append(values[i]);
        builder.append(")");
        return new String(builder);
    }

    static public VecnValue pointwise(VecnValue x, Function<Float, Float> f) {
        VecnValue result = new VecnValue(x.getN());
        for (int i = 0; i < x.getN(); i++)
            result.values[i] = f.apply(x.values[i]);
        return result;
    }

    static public VecnValue pointwise(VecnValue x, VecnValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.values[i]);
        return res;
    }

    static public VecnValue pointwise(VecnValue x, FloatValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.value);
        return res;
    }

    static public VecnValue pointwise(FloatValue x, VecnValue y, BiFunction<Float, Float, Float> f) {
        VecnValue res = new VecnValue(y.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.value, y.values[i]);
        return res;
    }

    @Override
    public Value valueAt(int i) throws InvalidIndexException {
        if (i < 0 || i >= values.length) throw InvalidIndexException.outOfRange();
        return new FloatValue(values[i]);
    }
}

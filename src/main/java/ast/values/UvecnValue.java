package ast.values;

import ast.exceptions.InvalidIndexException;
import ast.types.UvecnType;

import java.util.*;
import java.util.function.*;

public class UvecnValue extends Value implements Vectorized, Indexed {
    public long[] values = null;

    public UvecnValue(int n) {
        values = new long[n];
        type = UvecnType.fromN(n);
    }

    public UvecnValue(UvecnType type, UintValue v) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++) values[i] = v.value;
    }

    public UvecnValue(UvecnType type, List<UintValue> values) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++)
            this.values[i] = values.get(i).value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("uvec" + values.length + "(" + values[0]);
        for (int i = 1; i < values.length; i++) builder.append(", ").append(values[i]);
        builder.append(")");
        return new String(builder);
    }

    public int getN() {
        return values.length;
    }

    @Override
    public Value[] retrieve() {
        var result = new Value[getN()];
        for (int i = 0; i < result.length; i++)
            result[i] = new UintValue(values[i]);
        return result;
    }

    static public UvecnValue pointwise(UvecnValue x, Function<Long, Long> f) {
        UvecnValue result = new UvecnValue(x.getN());
        for (int i = 0; i < x.getN(); i++)
            result.values[i] = f.apply(x.values[i]);
        return result;
    }

    static public UvecnValue pointwise(UvecnValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.values[i]);
        return res;
    }

    static public UvecnValue pointwise(UvecnValue x, UintValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.value);
        return res;
    }

    static public UvecnValue pointwise(UintValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(y.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.value, y.values[i]);
        return res;
    }

    @Override
    public Value valueAt(int i) throws InvalidIndexException {
        if (i < 0 || i >= values.length) throw InvalidIndexException.outOfRange();
        return new UintValue(values[i]);
    }
}

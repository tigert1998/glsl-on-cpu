package ast.values;

import java.util.function.*;

public class UvecnValue extends Value {
    public Long[] value = null;

    public UvecnValue(int n) {
        value = new Long[n];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("uvec" + value.length + "(" + value[0]);
        for (int i = 1; i < value.length; i++) builder.append(", ").append(value[i]);
        builder.append(")");
        return new String(builder);
    }

    public int getN() {
        return value.length;
    }

    static public UvecnValue pointwise(UvecnValue x, Function<Long, Long> f) {
        UvecnValue result = new UvecnValue(x.getN());
        for (int i = 0; i < x.getN(); i++)
            result.value[i] = f.apply(x.value[i]);
        return result;
    }

    static public UvecnValue pointwise(UvecnValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value[i], y.value[i]);
        return res;
    }

    static public UvecnValue pointwise(UvecnValue x, UintValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value[i], y.value);
        return res;
    }

    static public UvecnValue pointwise(UintValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(y.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value, y.value[i]);
        return res;
    }
}

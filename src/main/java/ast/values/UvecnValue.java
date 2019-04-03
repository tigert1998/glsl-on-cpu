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

    public UvecnValue map(Function<Long, Long> f) {
        UvecnValue result = new UvecnValue(value.length);
        for (int i = 0; i < value.length; i++)
            result.value[i] = f.apply(value[i]);
        return result;
    }

    static public UvecnValue applyFunction(UvecnValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.value.length);
        for (int i = 0; i < res.value.length; i++)
            res.value[i] = f.apply(x.value[i], y.value[i]);
        return res;
    }

    static public UvecnValue applyFunction(UvecnValue x, UintValue y, BiFunction<Long, Long, Long> f, boolean flipped) {
        UvecnValue res = new UvecnValue(x.value.length);
        if (!flipped) {
            for (int i = 0; i < res.value.length; i++)
                res.value[i] = f.apply(x.value[i], y.value);
        } else {
            for (int i = 0; i < res.value.length; i++)
                res.value[i] = f.apply(y.value, x.value[i]);
        }
        return res;
    }
}

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

    static public UvecnValue applyFunction(UvecnValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.value.length);
        for (int i = 0; i < res.value.length; i++)
            res.value[i] = f.apply(x.value[i], y.value[i]);
        return res;
    }

    static public UvecnValue applyFunction(UvecnValue x, UintValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.value.length);
        for (int i = 0; i < res.value.length; i++)
            res.value[i] = f.apply(x.value[i], y.value);
        return res;
    }
}

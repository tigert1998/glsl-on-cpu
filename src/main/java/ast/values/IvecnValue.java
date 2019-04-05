package ast.values;

import ast.types.*;

import java.util.function.*;

public class IvecnValue extends Value {
    public Integer[] value = null;

    public IvecnValue(int n) {
        this.type = IvecnType.fromN(n);
        value = new Integer[n];
    }

    public int getN() {
        return value.length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ivec" + value.length + "(" + value[0]);
        for (int i = 1; i < value.length; i++) builder.append(", ").append(value[i]);
        builder.append(")");
        return new String(builder);
    }

    static public IvecnValue pointwise(IvecnValue x, Function<Integer, Integer> f) {
        IvecnValue result = new IvecnValue(x.getN());
        for (int i = 0; i < x.getN(); i++)
            result.value[i] = f.apply(x.value[i]);
        return result;
    }

    static public IvecnValue pointwise(IvecnValue x, IvecnValue y, BiFunction<Integer, Integer, Integer> f) {
        IvecnValue res = new IvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value[i], y.value[i]);
        return res;
    }

    static public IvecnValue pointwise(IvecnValue x, IntValue y, BiFunction<Integer, Integer, Integer> f) {
        IvecnValue res = new IvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value[i], y.value);
        return res;
    }

    static public IvecnValue pointwise(IntValue x, IvecnValue y, BiFunction<Integer, Integer, Integer> f) {
        IvecnValue res = new IvecnValue(y.getN());
        for (int i = 0; i < res.getN(); i++)
            res.value[i] = f.apply(x.value, y.value[i]);
        return res;
    }
}

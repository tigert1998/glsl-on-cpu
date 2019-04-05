package ast.values;

import ast.types.*;

import java.util.function.*;

public class MatnxmValue extends Value {
    public Float[][] value = null;

    public MatnxmValue(int n, int m) {
        this.type = MatnxmType.fromNM(n, m);
        value = new Float[n][m];
    }

    public int getN() {
        return ((MatnxmType) type).getN();
    }

    public int getM() {
        return ((MatnxmType) type).getM();
    }

    static public MatnxmValue pointwise(MatnxmValue x, Function<Float, Float> f) {
        int n = x.getN(), m = x.getM();
        MatnxmValue result = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                result.value[i][j] = f.apply(x.value[i][j]);
        return result;
    }

    static public MatnxmValue pointwise(MatnxmValue x, MatnxmValue y, BiFunction<Float, Float, Float> f) {
        int n = x.getN(), m = x.getM();
        MatnxmValue res = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                res.value[i][j] = f.apply(x.value[i][j], y.value[i][j]);
        return res;
    }

    static public MatnxmValue pointwise(MatnxmValue x, FloatValue y, BiFunction<Float, Float, Float> f) {
        int n = x.getN(), m = x.getM();
        MatnxmValue res = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                res.value[i][j] = f.apply(x.value[i][j], y.value);
        return res;
    }

    static public MatnxmValue pointwise(FloatValue x, MatnxmValue y, BiFunction<Float, Float, Float> f) {
        int n = y.getN(), m = y.getM();
        MatnxmValue res = new MatnxmValue(n, m);
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++)
                    res.value[i][j] = f.apply(x.value, y.value[i][j]);
        return res;
    }
}

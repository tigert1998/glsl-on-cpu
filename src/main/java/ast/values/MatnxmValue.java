package ast.values;

import ast.types.*;

import java.util.*;
import java.util.function.*;

public class MatnxmValue extends Value implements Vectorized {
    public float[][] values = null;

    public MatnxmValue(int n, int m) {
        this.type = MatnxmType.fromNM(n, m);
        values = new float[n][m];
    }

    public MatnxmValue(MatnxmType type, FloatValue v) {
        this(type.getN(), type.getM());
        for (int i = 0; i < Math.min(getN(), getM()); i++)
            values[i][i] = v.value;
    }

    public MatnxmValue(MatnxmType type, MatnxmValue v) {
        this(type.getN(), type.getM());
        for (int i = 0; i < Math.min(getN(), v.getN()); i++)
            for (int j = 0; j < Math.min(getM(), v.getM()); j++)
                values[i][j] = v.values[i][j];
    }

    public MatnxmValue(MatnxmType type, List<FloatValue> values) {
        this(type.getN(), type.getM());
        for (int i = 0; i < getN(); i++)
            for (int j = 0; j < getM(); j++)
                this.values[i][j] = values.get(i * getM() + j).value;
    }

    public int getN() {
        return ((MatnxmType) type).getN();
    }

    public int getM() {
        return ((MatnxmType) type).getM();
    }

    @Override
    public Value[] retrieve() {
        var result = new Value[getN() * getM()];
        for (int i = 0; i < getN(); i++)
            for (int j = 0; j < getM(); j++)
                result[i * getM() + j] = new FloatValue(values[i][j]);
        return result;
    }

    static public MatnxmValue pointwise(MatnxmValue x, Function<Float, Float> f) {
        int n = x.getN(), m = x.getM();
        MatnxmValue result = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                result.values[i][j] = f.apply(x.values[i][j]);
        return result;
    }

    static public MatnxmValue pointwise(MatnxmValue x, MatnxmValue y, BiFunction<Float, Float, Float> f) {
        int n = x.getN(), m = x.getM();
        MatnxmValue res = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                res.values[i][j] = f.apply(x.values[i][j], y.values[i][j]);
        return res;
    }

    static public MatnxmValue pointwise(MatnxmValue x, FloatValue y, BiFunction<Float, Float, Float> f) {
        int n = x.getN(), m = x.getM();
        MatnxmValue res = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                res.values[i][j] = f.apply(x.values[i][j], y.value);
        return res;
    }

    static public MatnxmValue pointwise(FloatValue x, MatnxmValue y, BiFunction<Float, Float, Float> f) {
        int n = y.getN(), m = y.getM();
        MatnxmValue res = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                res.values[i][j] = f.apply(x.value, y.values[i][j]);
        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < getM(); i++) {
            for (int j = 0; j < getN(); j++) {
                sb.append(values[j][i]);
                if (j < getN() - 1) sb.append(", ");
            }
            if (i < getM() - 1) sb.append("; ");
        }
        sb.append("]: ").append(getType());
        return new String(sb);
    }
}

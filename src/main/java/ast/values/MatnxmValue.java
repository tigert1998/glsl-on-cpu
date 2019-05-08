package ast.values;

import ast.exceptions.*;
import ast.types.*;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.*;
import java.util.function.*;

public class MatnxmValue extends Value implements Vectorized, Indexed {
    public float[][] values;

    public MatnxmValue(int n, int m) {
        this.type = MatnxmType.fromNM(n, m);
        values = new float[n][m];
    }

    public MatnxmValue(MatnxmType type, FloatValue v, boolean diagonal) {
        this(type.getN(), type.getM());
        if (diagonal) {
            for (int i = 0; i < Math.min(getN(), getM()); i++)
                values[i][i] = v.value;
        } else {
            for (int i = 0; i < getN(); i++)
                for (int j = 0; j < getM(); j++)
                    values[i][j] = v.value;
        }
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
        sb.append("]");
        return new String(sb);
    }

    @Override
    public Value valueAt(int i) throws InvalidIndexException {
        if (i < 0 || i >= getN()) throw InvalidIndexException.outOfRange();
        var result = new VecnValue(getM());
        for (int j = 0; j < getM(); j++)
            result.values[j] = values[i][j];
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MatnxmValue)) return false;
        var matnxm = (MatnxmValue) obj;
        if (!matnxm.getType().equals(this.getType())) return false;
        for (int i = 0; i < getN(); i++)
            for (int j = 0; j < getM(); j++)
                if (values[i][j] != matnxm.values[i][j]) return false;
        return true;
    }

    @Override
    public LLVMValueRef inLLVM() {
        return Vectorized.inLLVM(this);
    }
}

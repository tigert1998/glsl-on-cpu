package ast.operators;

import ast.exceptions.*;
import ast.values.*;

public class Mult extends Operator implements BinaryOperator {
    public static Mult OP = new Mult();

    // scalar
    protected Value apply(IntValue x, IntValue y) {
        return new IntValue(x.value * y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new UintValue(x.value * y.value);
    }

    protected Value apply(FloatValue x, FloatValue y) {
        return new FloatValue(x.value * y.value);
    }

    // ivecn
    protected Value apply(IvecnValue x, IvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return IvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(IntValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    // uvecn
    protected Value apply(UvecnValue x, UvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return UvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(UintValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    // vecn
    protected Value apply(VecnValue x, VecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return VecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(FloatValue x, VecnValue y) {
        return VecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(VecnValue x, FloatValue y) {
        return VecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    // matnxm
    protected Value apply(FloatValue x, MatnxmValue y) {
        return MatnxmValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(MatnxmValue x, FloatValue y) {
        return MatnxmValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected Value apply(VecnValue x, MatnxmValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() == y.getM()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        int n = y.getN(), l = x.getN();
        var result = new VecnValue(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < l; j++)
                result.value[i] += x.value[j] * y.value[i][j];
        return result;
    }

    protected Value apply(MatnxmValue x, VecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        int n = x.getM(), l = x.getN();
        var result = new VecnValue(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < l; j++)
                result.value[i] += x.value[j][i] * y.value[j];
        return result;
    }

    protected Value apply(MatnxmValue x, MatnxmValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getM()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        int l = x.getN(), m = x.getM(), n = y.getN();
        var result = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result.value[i][j] = 0.f;
                for (int k = 0; k < l; k++)
                    result.value[i][j] += x.value[k][j] * y.value[i][k];
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "*";
    }
}

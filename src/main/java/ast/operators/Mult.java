package ast.operators;

import ast.exceptions.*;
import ast.values.*;
import ast.types.*;

public class Mult extends Operator implements BinaryOperator {
    public static Mult OP = new Mult();

    // == for values ==
    // scalar
    protected IntValue apply(IntValue x, IntValue y) {
        return new IntValue(x.value * y.value);
    }

    protected UintValue apply(UintValue x, UintValue y) {
        return new UintValue(x.value * y.value);
    }

    protected FloatValue apply(FloatValue x, FloatValue y) {
        return new FloatValue(x.value * y.value);
    }

    // ivecn
    protected IvecnValue apply(IvecnValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected IvecnValue apply(IntValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected IvecnValue apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    // uvecn
    protected UvecnValue apply(UvecnValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected UvecnValue apply(UintValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected UvecnValue apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    // vecn
    protected VecnValue apply(VecnValue x, VecnValue y) {
        return VecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected VecnValue apply(FloatValue x, VecnValue y) {
        return VecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected VecnValue apply(VecnValue x, FloatValue y) {
        return VecnValue.pointwise(x, y, (a, b) -> a * b);
    }

    // matnxm
    protected MatnxmValue apply(FloatValue x, MatnxmValue y) {
        return MatnxmValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected MatnxmValue apply(MatnxmValue x, FloatValue y) {
        return MatnxmValue.pointwise(x, y, (a, b) -> a * b);
    }

    protected VecnValue apply(VecnValue x, MatnxmValue y) {
        int n = y.getN(), l = x.getN();
        var result = new VecnValue(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < l; j++)
                result.values[i] += x.values[j] * y.values[i][j];
        return result;
    }

    protected VecnValue apply(MatnxmValue x, VecnValue y) {
        int n = x.getM(), l = x.getN();
        var result = new VecnValue(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < l; j++)
                result.values[i] += x.values[j][i] * y.values[j];
        return result;
    }

    protected MatnxmValue apply(MatnxmValue x, MatnxmValue y) {
        int l = x.getN(), m = x.getM(), n = y.getN();
        var result = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result.values[i][j] = 0.f;
                for (int k = 0; k < l; k++)
                    result.values[i][j] += x.values[k][j] * y.values[i][k];
            }
        }
        return result;
    }

    // == for types ==
    // scalar
    protected IntType apply(IntType x, IntType y) {
        return x;
    }

    protected UintType apply(UintType x, UintType y) {
        return x;
    }

    protected FloatType apply(FloatType x, FloatType y) {
        return x;
    }

    // ivecn
    protected IvecnType apply(IvecnType x, IvecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    protected IvecnType apply(IntType x, IvecnType y) {
        return y;
    }

    protected IvecnType apply(IvecnType x, IntType y) {
        return x;
    }

    // uvecn
    protected UvecnType apply(UvecnType x, UvecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    protected UvecnType apply(UintType x, UvecnType y) {
        return y;
    }

    protected UvecnType apply(UvecnType x, UintType y) {
        return x;
    }

    // vecn
    protected VecnType apply(VecnType x, VecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    protected VecnType apply(FloatType x, VecnType y) {
        return y;
    }

    protected VecnType apply(VecnType x, FloatType y) {
        return x;
    }

    // matnxm
    protected MatnxmType apply(FloatType x, MatnxmType y) {
        return y;
    }

    protected MatnxmType apply(MatnxmType x, FloatType y) {
        return x;
    }

    protected VecnType apply(VecnType x, MatnxmType y) throws OperatorCannotBeAppliedException {
        if (x.getN() == y.getM()) throw new OperatorCannotBeAppliedException(this, x, y);
        return VecnType.fromN(y.getN());
    }

    protected VecnType apply(MatnxmType x, VecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return VecnType.fromN(x.getM());
    }

    protected MatnxmType apply(MatnxmType x, MatnxmType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getM()) throw new OperatorCannotBeAppliedException(this, x, y);
        return MatnxmType.fromNM(x.getM(), y.getN());
    }

    @Override
    public String toString() {
        return "*";
    }
}

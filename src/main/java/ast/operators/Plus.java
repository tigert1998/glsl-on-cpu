package ast.operators;

import ast.exceptions.*;
import ast.values.*;

public class Plus extends Operator implements UnaryOperator, BinaryOperator {
    public static Plus OP = new Plus();

    // unary operator
    protected Value apply(IntValue x) {
        return x;
    }

    protected Value apply(UintValue x) {
        return x;
    }

    protected Value apply(FloatValue x) {
        return x;
    }

    protected Value apply(VecnValue x) {
        return x;
    }

    protected Value apply(IvecnValue x) {
        return x;
    }

    protected Value apply(UvecnValue x) {
        return x;
    }

    protected Value apply(MatnxmValue x) {
        return x;
    }

    // binary operator
    // scalar
    protected Value apply(IntValue x, IntValue y) {
        return new IntValue(x.value + y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new UintValue(x.value + y.value);
    }

    protected Value apply(FloatValue x, FloatValue y) {
        return new FloatValue(x.value + y.value);
    }

    // ivecn
    protected Value apply(IvecnValue x, IvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return IvecnValue.pointwise(x, y, Integer::sum);
    }

    protected Value apply(IntValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, Integer::sum);
    }

    protected Value apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, Integer::sum);
    }

    // uvecn
    protected Value apply(UvecnValue x, UvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return UvecnValue.pointwise(x, y, Long::sum);
    }

    protected Value apply(UintValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, Long::sum);
    }

    protected Value apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, Long::sum);
    }

    // vecn
    protected Value apply(VecnValue x, VecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return VecnValue.pointwise(x, y, Float::sum);
    }

    protected Value apply(FloatValue x, VecnValue y) {
        return VecnValue.pointwise(x, y, Float::sum);
    }

    protected Value apply(VecnValue x, FloatValue y) {
        return VecnValue.pointwise(x, y, Float::sum);
    }

    // matnxm
    protected Value apply(FloatValue x, MatnxmValue y) {
        return MatnxmValue.pointwise(x, y, Float::sum);
    }

    protected Value apply(MatnxmValue x, FloatValue y) {
        return MatnxmValue.pointwise(x, y, Float::sum);
    }

    protected Value apply(MatnxmValue x, MatnxmValue y) throws OperatorCannotBeAppliedException {
        if (!x.getType().equals(y.getType()))
            throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return MatnxmValue.pointwise(x, y, Float::sum);
    }

    @Override
    public String toString() {
        return "+";
    }
}

package ast.operators;

import ast.exceptions.OperatorCannotBeAppliedException;
import ast.values.*;

public class Div extends Operator implements BinaryOperator {
    public static Div OP = new Div();

    // scalar
    protected Value apply(IntValue x, IntValue y) {
        return new IntValue(x.value / y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new UintValue(x.value / y.value);
    }

    protected Value apply(FloatValue x, FloatValue y) {
        return new FloatValue(x.value / y.value);
    }

    // ivecn
    protected Value apply(IvecnValue x, IvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return IvecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(IntValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    // uvecn
    protected Value apply(UvecnValue x, UvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return UvecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(UintValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    // vecn
    protected Value apply(VecnValue x, VecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return VecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(FloatValue x, VecnValue y) {
        return VecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(VecnValue x, FloatValue y) {
        return VecnValue.pointwise(x, y, (a, b) -> a / b);
    }

    // matnxm
    protected Value apply(FloatValue x, MatnxmValue y) {
        return MatnxmValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(MatnxmValue x, FloatValue y) {
        return MatnxmValue.pointwise(x, y, (a, b) -> a / b);
    }

    protected Value apply(MatnxmValue x, MatnxmValue y) throws OperatorCannotBeAppliedException {
        if (!x.getType().equals(y.getType()))
            throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return MatnxmValue.pointwise(x, y, (a, b) -> a / b);
    }

    @Override
    public String toString() {
        return "/";
    }
}
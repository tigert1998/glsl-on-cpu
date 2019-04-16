package ast.operators;

import ast.exceptions.*;
import ast.values.*;

public class BitwiseOr extends Operator implements BinaryOperator {
    public static BitwiseOr OP = new BitwiseOr();

    // scalar and scalar
    protected Value apply(IntValue x, IntValue y) {
        return new IntValue(x.value | y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new UintValue(x.value | y.value);
    }

    // scalar and vector
    protected Value apply(IntValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a | b);
    }

    protected Value apply(UintValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a | b);
    }

    // vector and scalar
    protected Value apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a | b);
    }

    protected Value apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a | b);
    }

    // vector and vector
    protected Value apply(IvecnValue x, IvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return IvecnValue.pointwise(x, y, (a, b) -> a | b);
    }

    protected Value apply(UvecnValue x, UvecnValue y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return UvecnValue.pointwise(x, y, (a, b) -> a | b);
    }

    @Override
    public String toString() {
        return "|";
    }
}

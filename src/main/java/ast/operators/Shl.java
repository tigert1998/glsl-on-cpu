package ast.operators;

import ast.values.*;

public class Shl extends Operator implements BinaryOperator {
    public static Shl OP = new Shl();

    // scalar
    protected Value apply(IntValue x, IntValue y) {
        return new IntValue(x.value << y.value);
    }

    protected Value apply(IntValue x, UintValue y) {
        return new IntValue(x.value << y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new UintValue(x.value << y.value);
    }

    protected Value apply(UintValue x, IntValue y) {
        return new UintValue(x.value << y.value);
    }

    // vector and scalar
    protected Value apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected Value apply(IvecnValue x, UintValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected Value apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected Value apply(UvecnValue x, IntValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    // vector and vector
    protected Value apply(IvecnValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected Value apply(IvecnValue x, UvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected Value apply(UvecnValue x, IvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected Value apply(UvecnValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    @Override
    public String toString() {
        return "<<";
    }
}

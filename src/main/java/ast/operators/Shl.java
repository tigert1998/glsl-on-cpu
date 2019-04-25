package ast.operators;

import ast.exceptions.*;
import ast.values.*;
import ast.types.*;

public class Shl extends Operator implements BinaryOperator {
    public static Shl OP = new Shl();

    // == for values ==
    // scalar
    protected IntValue apply(IntValue x, IntValue y) {
        return new IntValue(x.value << y.value);
    }

    protected IntValue apply(IntValue x, UintValue y) {
        return new IntValue(x.value << y.value);
    }

    protected UintValue apply(UintValue x, UintValue y) {
        return new UintValue(x.value << y.value);
    }

    protected UintValue apply(UintValue x, IntValue y) {
        return new UintValue(x.value << y.value);
    }

    // vector and scalar
    protected IvecnValue apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected IvecnValue apply(IvecnValue x, UintValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected UvecnValue apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected UvecnValue apply(UvecnValue x, IntValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    // vector and vector
    protected IvecnValue apply(IvecnValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected IvecnValue apply(IvecnValue x, UvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected UvecnValue apply(UvecnValue x, IvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    protected UvecnValue apply(UvecnValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a << b);
    }

    // == for types ==
    // scalar
    protected IntType apply(IntType x, IntType y) {
        return x;
    }

    protected IntType apply(IntType x, UintType y) {
        return x;
    }

    protected UintType apply(UintType x, UintType y) {
        return x;
    }

    protected UintType apply(UintType x, IntType y) {
        return x;
    }

    // vector and scalar
    protected IvecnType apply(IvecnType x, IntType y) {
        return x;
    }

    protected IvecnType apply(IvecnType x, UintType y) {
        return x;
    }

    protected UvecnType apply(UvecnType x, UintType y) {
        return x;
    }

    protected UvecnType apply(UvecnType x, IntType y) {
        return x;
    }

    // vector and vector
    protected IvecnType apply(IvecnType x, IvecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    protected IvecnType apply(IvecnType x, UvecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    protected UvecnType apply(UvecnType x, IvecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    protected UvecnType apply(UvecnType x, UvecnType y) throws OperatorCannotBeAppliedException {
        if (x.getN() != y.getN()) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    @Override
    public String toString() {
        return "<<";
    }
}

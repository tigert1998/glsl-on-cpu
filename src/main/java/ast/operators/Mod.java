package ast.operators;

import ast.exceptions.*;
import ast.values.*;
import ast.types.*;

public class Mod extends Operator implements BinaryOperator {
    public static Mod OP = new Mod();

    // == for values ==
    // scalar
    protected IntValue apply(IntValue x, IntValue y) {
        return new IntValue(x.value % y.value);
    }

    protected UintValue apply(UintValue x, UintValue y) {
        return new UintValue(x.value % y.value);
    }

    // ivecn
    protected IvecnValue apply(IvecnValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a % b);
    }

    protected IvecnValue apply(IntValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a % b);
    }

    protected IvecnValue apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, (a, b) -> a % b);
    }

    // uvecn
    protected UvecnValue apply(UvecnValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a % b);
    }

    protected UvecnValue apply(UintValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a % b);
    }

    protected UvecnValue apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, (a, b) -> a % b);
    }

    // == for types ==
    // scalar
    protected IntType apply(IntType x, IntType y) {
        return x;
    }

    protected UintType apply(UintType x, UintType y) {
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

    @Override
    public String toString() {
        return "%";
    }
}

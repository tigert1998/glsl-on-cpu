package ast.operators;

import ast.exceptions.*;
import ast.values.*;
import ast.types.*;

public class Plus extends Operator implements UnaryOperator, BinaryOperator {
    public static Plus OP = new Plus();

    // == for values ==
    // unary operator
    protected IntValue apply(IntValue x) {
        return x;
    }

    protected UintValue apply(UintValue x) {
        return x;
    }

    protected FloatValue apply(FloatValue x) {
        return x;
    }

    protected VecnValue apply(VecnValue x) {
        return x;
    }

    protected IvecnValue apply(IvecnValue x) {
        return x;
    }

    protected UvecnValue apply(UvecnValue x) {
        return x;
    }

    protected MatnxmValue apply(MatnxmValue x) {
        return x;
    }

    // binary operator
    // scalar
    protected IntValue apply(IntValue x, IntValue y) {
        return new IntValue(x.value + y.value);
    }

    protected UintValue apply(UintValue x, UintValue y) {
        return new UintValue(x.value + y.value);
    }

    protected FloatValue apply(FloatValue x, FloatValue y) {
        return new FloatValue(x.value + y.value);
    }

    // ivecn
    protected IvecnValue apply(IvecnValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, Integer::sum);
    }

    protected IvecnValue apply(IntValue x, IvecnValue y) {
        return IvecnValue.pointwise(x, y, Integer::sum);
    }

    protected IvecnValue apply(IvecnValue x, IntValue y) {
        return IvecnValue.pointwise(x, y, Integer::sum);
    }

    // uvecn
    protected UvecnValue apply(UvecnValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, Long::sum);
    }

    protected UvecnValue apply(UintValue x, UvecnValue y) {
        return UvecnValue.pointwise(x, y, Long::sum);
    }

    protected UvecnValue apply(UvecnValue x, UintValue y) {
        return UvecnValue.pointwise(x, y, Long::sum);
    }

    // vecn
    protected VecnValue apply(VecnValue x, VecnValue y) {
        return VecnValue.pointwise(x, y, Float::sum);
    }

    protected VecnValue apply(FloatValue x, VecnValue y) {
        return VecnValue.pointwise(x, y, Float::sum);
    }

    protected VecnValue apply(VecnValue x, FloatValue y) {
        return VecnValue.pointwise(x, y, Float::sum);
    }

    // matnxm
    protected MatnxmValue apply(FloatValue x, MatnxmValue y) {
        return MatnxmValue.pointwise(x, y, Float::sum);
    }

    protected MatnxmValue apply(MatnxmValue x, FloatValue y) {
        return MatnxmValue.pointwise(x, y, Float::sum);
    }

    protected MatnxmValue apply(MatnxmValue x, MatnxmValue y) {
        return MatnxmValue.pointwise(x, y, Float::sum);
    }

    // == for types ==
    // unary operator
    protected IntType apply(IntType x) {
        return x;
    }

    protected UintType apply(UintType x) {
        return x;
    }

    protected FloatType apply(FloatType x) {
        return x;
    }

    protected VecnType apply(VecnType x) {
        return x;
    }

    protected IvecnType apply(IvecnType x) {
        return x;
    }

    protected UvecnType apply(UvecnType x) {
        return x;
    }

    protected MatnxmType apply(MatnxmType x) {
        return x;
    }

    // binary operator
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

    protected MatnxmType apply(MatnxmType x, MatnxmType y) throws OperatorCannotBeAppliedException {
        if (!x.equals(y)) throw new OperatorCannotBeAppliedException(this, x, y);
        return x;
    }

    @Override
    public String toString() {
        return "+";
    }
}

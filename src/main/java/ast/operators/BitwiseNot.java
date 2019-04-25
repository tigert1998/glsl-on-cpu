package ast.operators;

import ast.types.*;
import ast.values.*;

public class BitwiseNot extends Operator implements UnaryOperator {
    static public BitwiseNot OP = new BitwiseNot();

    // == for values ==
    protected IntValue apply(IntValue x) {
        return new IntValue(~x.value);
    }

    protected UintValue apply(UintValue x) {
        return new UintValue(~x.value);
    }

    protected IvecnValue apply(IvecnValue x) {
        return IvecnValue.pointwise(x, a -> ~a);
    }

    protected UvecnValue apply(UvecnValue x) {
        return UvecnValue.pointwise(x, a -> ~a);
    }

    // == for types ==
    protected IntType apply(IntType x) {
        return x;
    }

    protected UintType apply(UintType x) {
        return x;
    }

    protected IvecnType apply(IvecnType x) {
        return x;
    }

    protected UvecnType apply(UvecnType x) {
        return x;
    }

    @Override
    public String toString() {
        return "~";
    }
}

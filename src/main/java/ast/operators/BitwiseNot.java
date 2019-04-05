package ast.operators;

import ast.values.*;

public class BitwiseNot extends Operator implements UnaryOperator {
    static public BitwiseNot OP = new BitwiseNot();

    protected Value apply(IntValue x) {
        return new IntValue(~x.value);
    }

    protected Value apply(UintValue x) {
        return new UintValue(~x.value);
    }

    protected Value apply(IvecnValue x) {
        return IvecnValue.pointwise(x, a -> ~a);
    }

    protected Value apply(UvecnValue x) {
        return UvecnValue.pointwise(x, a -> ~a);
    }

    @Override
    public String toString() {
        return "~";
    }
}

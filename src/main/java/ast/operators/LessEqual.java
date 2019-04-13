package ast.operators;

import ast.values.*;

public class LessEqual extends Operator implements BinaryOperator {
    public static LessEqual OP = new LessEqual();

    // scalar
    protected Value apply(IntValue x, IntValue y) {
        return new BoolValue(x.value <= y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new BoolValue(x.value <= y.value);
    }

    protected Value apply(FloatValue x, FloatValue y) {
        return new BoolValue(x.value <= y.value);
    }

    @Override
    public String toString() {
        return "<=";
    }
}

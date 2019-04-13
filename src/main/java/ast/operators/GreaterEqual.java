package ast.operators;

import ast.values.*;

public class GreaterEqual extends Operator implements BinaryOperator {
    public static GreaterEqual OP = new GreaterEqual();

    protected Value apply(IntValue x, IntValue y) {
        return new BoolValue(x.value >= y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new BoolValue(x.value >= y.value);
    }

    protected Value apply(FloatValue x, FloatValue y) {
        return new BoolValue(x.value >= y.value);
    }

    @Override
    public String toString() {
        return ">=";
    }
}

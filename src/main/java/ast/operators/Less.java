package ast.operators;

import ast.values.*;

public class Less extends Operator implements BinaryOperator {
    public static Less OP = new Less();

    // scalar
    protected Value apply(IntValue x, IntValue y) {
        return new BoolValue(x.value < y.value);
    }

    protected Value apply(UintValue x, UintValue y) {
        return new BoolValue(x.value < y.value);
    }

    protected Value apply(FloatValue x, FloatValue y) {
        return new BoolValue(x.value < y.value);
    }

    @Override
    public String toString() {
        return "<";
    }
}

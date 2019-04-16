package ast.operators;

import ast.values.*;

public class LogicalAnd extends Operator implements BinaryOperator {
    public static LogicalAnd OP = new LogicalAnd();

    protected Value apply(BoolValue x, BoolValue y) {
        return new BoolValue(x.value && y.value);
    }

    @Override
    public String toString() {
        return "&&";
    }
}

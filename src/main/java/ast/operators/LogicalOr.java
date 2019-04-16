package ast.operators;

import ast.values.*;

public class LogicalOr extends Operator implements BinaryOperator {
    public static LogicalOr OP = new LogicalOr();

    protected Value apply(BoolValue x, BoolValue y) {
        return new BoolValue(x.value || y.value);
    }

    @Override
    public String toString() {
        return "||";
    }
}

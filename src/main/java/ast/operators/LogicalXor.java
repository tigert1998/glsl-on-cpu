package ast.operators;

import ast.values.*;

public class LogicalXor extends Operator implements BinaryOperator {
    public static LogicalXor OP = new LogicalXor();

    protected Value apply(BoolValue x, BoolValue y) {
        return new BoolValue(x.value ^ y.value);
    }

    @Override
    public String toString() {
        return "^^";
    }
}

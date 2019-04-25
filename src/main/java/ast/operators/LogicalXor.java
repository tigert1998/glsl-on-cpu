package ast.operators;

import ast.values.*;
import ast.types.*;

public class LogicalXor extends Operator implements BinaryOperator {
    public static LogicalXor OP = new LogicalXor();

    protected BoolValue apply(BoolValue x, BoolValue y) {
        return new BoolValue(x.value ^ y.value);
    }

    protected BoolType apply(BoolType x, BoolType y) {
        return x;
    }

    @Override
    public String toString() {
        return "^^";
    }
}

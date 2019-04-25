package ast.operators;

import ast.values.*;
import ast.types.*;

public class LogicalOr extends Operator implements BinaryOperator {
    public static LogicalOr OP = new LogicalOr();

    protected BoolValue apply(BoolValue x, BoolValue y) {
        return new BoolValue(x.value || y.value);
    }

    protected BoolType apply(BoolType x, BoolType y) {
        return x;
    }

    @Override
    public String toString() {
        return "||";
    }
}

package ast.operators;

import ast.types.*;
import ast.values.*;

public class LogicalAnd extends Operator implements BinaryOperator {
    public static LogicalAnd OP = new LogicalAnd();

    protected BoolValue apply(BoolValue x, BoolValue y) {
        return new BoolValue(x.value && y.value);
    }

    protected BoolType apply(BoolType x, BoolType y) {
        return x;
    }

    @Override
    public String toString() {
        return "&&";
    }
}

package ast.operators;

import ast.values.*;
import ast.types.*;

public class LogicalNot extends Operator implements UnaryOperator {
    static public LogicalNot OP = new LogicalNot();

    protected BoolValue apply(BoolValue x) {
        return new BoolValue(!x.value);
    }

    protected BoolType apply(BoolType x) {
        return x;
    }

    @Override
    public String toString() {
        return "!";
    }
}

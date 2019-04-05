package ast.operators;

import ast.values.*;

public class LogicalNot extends Operator implements UnaryOperator {
    static public LogicalNot OP = new LogicalNot();

    protected Value apply(BoolValue x) {
        return new BoolValue(!x.value);
    }

    @Override
    public String toString() {
        return "!";
    }
}

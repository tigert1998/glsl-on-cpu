package ast.operators;

import ast.exceptions.OperatorCannotBeAppliedException;
import ast.values.*;

public class NotEqual extends Operator implements BinaryOperator {
    public static NotEqual OP = new NotEqual();

    public Value apply(Value x, Value y) throws OperatorCannotBeAppliedException {
        if (!x.getType().equals(y.getType()))
            throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return new BoolValue(!x.equals(y));
    }

    @Override
    public String toString() {
        return "!=";
    }
}

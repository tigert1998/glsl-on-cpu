package ast.operators;

import ast.exceptions.*;
import ast.values.*;

public class Equal extends Operator implements BinaryOperator {
    public static Equal OP = new Equal();

    public Value apply(Value x, Value y) throws OperatorCannotBeAppliedException {
        if (!x.getType().equals(y.getType()))
            throw new OperatorCannotBeAppliedException(this, x.getType(), y.getType());
        return new BoolValue(x.equals(y));
    }

    @Override
    public String toString() {
        return "==";
    }
}

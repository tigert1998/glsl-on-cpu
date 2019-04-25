package ast.operators;

import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class Equal extends Operator implements BinaryOperator {
    public static Equal OP = new Equal();

    public Value apply(Value x, Value y) {
        return new BoolValue(x.equals(y));
    }

    public BoolType apply(Type x, Type y) throws OperatorCannotBeAppliedException {
        if (!x.equals(y))
            throw new OperatorCannotBeAppliedException(this, x, y);
        return BoolType.TYPE;
    }

    @Override
    public String toString() {
        return "==";
    }
}

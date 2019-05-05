package ast.operators;

import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class NotEqual extends Operator implements BinaryOperator {
    public static NotEqual OP = new NotEqual();

    public BoolValue apply(Value x, Value y) throws OperatorCannotBeAppliedException {
        apply(x.getType(), y.getType());
        return new BoolValue(!x.equals(y));
    }

    public BoolType apply(Type x, Type y) throws OperatorCannotBeAppliedException {
        if (!x.equals(y))
            throw new OperatorCannotBeAppliedException(this, x, y);
        return BoolType.TYPE;
    }

    @Override
    public String toString() {
        return "!=";
    }
}

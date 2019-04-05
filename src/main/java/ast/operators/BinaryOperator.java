package ast.operators;

import ast.exceptions.*;
import ast.values.*;

public interface BinaryOperator {
    default Value apply(Value x, Value y) throws OperatorCannotBeAppliedException {
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass(), y.getClass());
            return (Value) method.invoke(this, x, y);
        } catch (Exception exception) {
            throw new OperatorCannotBeAppliedException((Operator) this, x.getType(), y.getType());
        }
    }
}

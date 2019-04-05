package ast.operators;

import ast.exceptions.*;
import ast.values.*;

public interface UnaryOperator {
    default Value apply(Value x) throws OperatorCannotBeAppliedException {
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass());
            return (Value) method.invoke(this, x);
        } catch (Exception exception) {
            throw new OperatorCannotBeAppliedException((Operator) this, x.getType());
        }
    }
}

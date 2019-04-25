package ast.operators;

import ast.exceptions.*;
import ast.values.*;

import java.lang.reflect.InvocationTargetException;

public interface BinaryOperator {
    default Value apply(Value x, Value y) throws OperatorCannotBeAppliedException, ArithmeticException {
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass(), y.getClass());
            return (Value) method.invoke(this, x, y);
        } catch (InvocationTargetException exception) {
            if (exception.getCause() instanceof OperatorCannotBeAppliedException) {
                throw new OperatorCannotBeAppliedException((Operator) this, x.getType(), y.getType());
            } else {
                throw (ArithmeticException) exception.getCause();
            }
        } catch (Exception exception) {
            throw new OperatorCannotBeAppliedException((Operator) this, x.getType(), y.getType());
        }
    }
}

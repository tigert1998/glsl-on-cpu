package ast.operators;

import ast.exceptions.*;
import ast.values.*;
import ast.types.*;

import java.lang.reflect.InvocationTargetException;

public interface BinaryOperator {
    default Value apply(Value x, Value y) throws OperatorCannotBeAppliedException, ArithmeticException {
        // check syntax
        apply(x.getType(), y.getType());
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass(), y.getClass());
            return (Value) method.invoke(this, x, y);
        } catch (InvocationTargetException exception) {
            throw (ArithmeticException) exception.getCause();
        } catch (Exception ignore) {
            return null;
        }
    }

    default Type apply(Type x, Type y) throws OperatorCannotBeAppliedException {
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass(), y.getClass());
            return (Type) method.invoke(this, x, y);
        } catch (Exception exception) {
            throw new OperatorCannotBeAppliedException((Operator) this, x, y);
        }
    }
}

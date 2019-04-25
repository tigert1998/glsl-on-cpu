package ast.operators;

import ast.exceptions.*;
import ast.types.Type;
import ast.values.*;

import java.lang.reflect.InvocationTargetException;

public interface UnaryOperator {
    default Value apply(Value x) throws OperatorCannotBeAppliedException {
        // check syntax
        apply(x.getType());
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass());
            return (Value) method.invoke(this, x);
        } catch (InvocationTargetException exception) {
            throw (ArithmeticException) exception.getCause();
        } catch (Exception ignore) {
            return null;
        }
    }

    default Type apply(Type x) throws OperatorCannotBeAppliedException {
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass());
            return (Type) method.invoke(this, x);
        } catch (Exception exception) {
            throw new OperatorCannotBeAppliedException((Operator) this, x);
        }
    }
}

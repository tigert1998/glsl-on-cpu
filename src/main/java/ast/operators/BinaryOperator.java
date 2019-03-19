package ast.operators;

import ast.types.*;
import ast.*;
import ast.values.*;

public interface BinaryOperator {
    boolean canBeApplied(Type type1, Type type2);

    Value apply(Value value1, Value value2, Scope scope)
            throws NotLValueException, OperatorCannotBeAppliedException;

}

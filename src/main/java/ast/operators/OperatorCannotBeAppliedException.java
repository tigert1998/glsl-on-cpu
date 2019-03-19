package ast.operators;

import ast.types.*;

public class OperatorCannotBeAppliedException extends Exception {
    public OperatorCannotBeAppliedException(Operator operator, Type type) {
        super(operator + " cannot be applied on " + type);
    }

    public OperatorCannotBeAppliedException(Operator operator, Type type1, Type type2) {
        super(operator + " cannot be applied on " + type1 + " and " + type2);
    }
}

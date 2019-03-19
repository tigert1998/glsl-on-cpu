package ast.operators;

import ast.types.*;

public class OperatorCannotBeAppliedException extends Exception {
    public OperatorCannotBeAppliedException(String operatorName, Type type) {
        super(operatorName + " cannot be applied on " + type);
    }

    public OperatorCannotBeAppliedException(String operatorName, Type type1, Type type2) {
        super(operatorName + " cannot be applied on " + type1 + " and " + type2);
    }
}

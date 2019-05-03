package ast.exceptions;

import ast.operators.*;
import ast.types.*;

public class OperatorCannotBeAppliedException extends UnlocatedSyntaxErrorException {
    public OperatorCannotBeAppliedException(Operator operator, Type type) {
        super("'" + operator + "' cannot be applied on " + type);
    }

    // for ++ and --
    public OperatorCannotBeAppliedException(String operator, Type type) {
        super("'" + operator + "' cannot be applied on " + type);
    }

    public OperatorCannotBeAppliedException(Operator operator, Type type1, Type type2) {
        super("'" + operator + "' cannot be applied between " + type1 + " and " + type2);
    }
}

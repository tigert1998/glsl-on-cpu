package ast.exceptions;

import ast.operators.*;
import ast.types.*;
import org.antlr.v4.runtime.Token;

public class OperatorCannotBeAppliedException extends SyntaxErrorException {
    public OperatorCannotBeAppliedException(Token token, Operator operator, Type type) {
        super(token, "'" + operator + "' cannot be applied on " + type);
    }

    public OperatorCannotBeAppliedException(Token token, Operator operator, Type type1, Type type2) {
        super(token, "'" + operator + "' cannot be applied on " + type1 + " and " + type2);
    }
}

package ast.exceptions;

import ast.types.*;

public class UnlocatedSyntaxErrorException extends Exception {
    public UnlocatedSyntaxErrorException(String message) {
        super(message);
    }

    public static UnlocatedSyntaxErrorException cannotConvert(Type from, Type to) {
        return new UnlocatedSyntaxErrorException(Messages.cannotConvert(from, to));
    }

    public static UnlocatedSyntaxErrorException notBooleanExpression() {
        return new UnlocatedSyntaxErrorException(Messages.notBooleanExpression);
    }

    public static UnlocatedSyntaxErrorException notIntegerExpression() {
        return new UnlocatedSyntaxErrorException(Messages.notIntegerExpression);
    }

    public static UnlocatedSyntaxErrorException lvalueRequired() {
        return new UnlocatedSyntaxErrorException(Messages.lvalueRequired);
    }
}

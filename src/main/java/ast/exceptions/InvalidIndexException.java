package ast.exceptions;

import ast.types.*;

public class InvalidIndexException extends UnlocatedSyntaxErrorException {
    private InvalidIndexException(String message) {
        super(message);
    }

    public static InvalidIndexException outOfRange() {
        return new InvalidIndexException("field selection out of range");
    }

    public static InvalidIndexException invalidSubscriptingType(Type type) {
        return new InvalidIndexException(Messages.invalidSubscriptingType(type));
    }
}

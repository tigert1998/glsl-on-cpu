package ast.exceptions;

public class InvalidIndexException extends UnlocatedSyntaxErrorException {
    private InvalidIndexException(String message) {
        super(message);
    }

    public static InvalidIndexException outOfRange() {
        return new InvalidIndexException("field selection out of range");
    }
}

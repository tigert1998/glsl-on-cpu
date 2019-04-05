package ast.exceptions;

import org.antlr.v4.runtime.Token;

public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(int lineID, int columnID, String message) {
        super(lineID + ":" + columnID + ": " + message);
    }

    public SyntaxErrorException(Token token, String message) {
        this(token.getLine(), token.getCharPositionInLine(), message);
    }

    public static SyntaxErrorException undeclaredID(Token token, String id) {
        return new SyntaxErrorException(token,
                "'" + id + "': undeclared identifier");
    }

    public static SyntaxErrorException redefinition(Token token, String id) {
        return new SyntaxErrorException(token,
                "'" + id + "': redefinition");
    }

    public static SyntaxErrorException invalidArraySizeType(Token token) {
        return new SyntaxErrorException(token,
                "array size must be a constant integer expression");
    }

    public static SyntaxErrorException arrayOfArrays(Token token) {
        return new SyntaxErrorException(token,
                "cannot declare array of arrays");
    }

    public static SyntaxErrorException structArrayMemberUnknownSize(Token token, String id) {
        return new SyntaxErrorException(token,
                "'" + id + "': array members of structs must specify a size");
    }

    public static SyntaxErrorException lvalueRequired(Token token) {
        return new SyntaxErrorException(token,
                "l-value required (cannot modify a const)");
    }

    public static SyntaxErrorException embeddedStructDefinition(Token token) {
        return new SyntaxErrorException(token,
                "embedded struct definitions are not allowed");
    }

    public static SyntaxErrorException duplicateFieldName(Token token, String id) {
        return new SyntaxErrorException(token,
                "'" + id + "': duplicate field name in structure");
    }

    public static SyntaxErrorException arraySizeNotPositive(Token token) {
        return new SyntaxErrorException(token,
                "array size must be greater than zero");
    }
}

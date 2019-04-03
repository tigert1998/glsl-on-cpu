package ast.exceptions;

import org.antlr.v4.runtime.Token;

public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(int lineID, int columnID, String message) {
        super(lineID + ":" + columnID + ": " + message);
    }

    public SyntaxErrorException(Token errToken, String message) {
        this(errToken.getLine(), errToken.getCharPositionInLine(), message);
    }

    public static SyntaxErrorException undeclaredID(Token errToken, String id) {
        return new SyntaxErrorException(errToken,
                "'" + id + "': undeclared identifier");
    }

    public static SyntaxErrorException redefinition(Token errToken, String id) {
        return new SyntaxErrorException(errToken,
                "'" + id + "': redefinition");
    }

    public static SyntaxErrorException invalidArraySizeType(Token errToken) {
        return new SyntaxErrorException(errToken,
                "array size must be a constant integer expression");
    }

    public static SyntaxErrorException arrayOfArrays(Token errToken) {
        return new SyntaxErrorException(errToken,
                "cannot declare array of arrays");
    }

    public static SyntaxErrorException structArrayMemberUnknownSize(Token errToken, String id) {
        return new SyntaxErrorException(errToken,
                "'" + id + "': array members of structs must specify a size");
    }

    public static SyntaxErrorException lvalueRequired(Token errToken) {
        return new SyntaxErrorException(errToken,
                "l-value required (cannot modify a const)");
    }

    public static SyntaxErrorException embeddedStructDefinition(Token errToken) {
        return new SyntaxErrorException(errToken,
                "embedded struct definitions are not allowed");
    }

    public static SyntaxErrorException duplicateFieldName(Token errToken, String id) {
        return new SyntaxErrorException(errToken,
                "'" + id + "': duplicate field name in structure");
    }

    public static SyntaxErrorException arraySizeNotPositive(Token errToken) {
        return new SyntaxErrorException(errToken,
                "array size must be greater than zero");
    }
}

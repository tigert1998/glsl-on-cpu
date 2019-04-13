package ast.exceptions;

import ast.types.*;
import org.antlr.v4.runtime.Token;

public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(int lineID, int columnID, String message) {
        super(lineID + ":" + columnID + ": " + message);
    }

    public SyntaxErrorException(Token token, String message) {
        this(token.getLine(), token.getCharPositionInLine(), message);
    }

    public SyntaxErrorException(Token token, UnlocatedSyntaxErrorException exception) {
        this(token, exception.getMessage());
    }

    public static SyntaxErrorException undeclaredID(Token token, String id) {
        return new SyntaxErrorException(token,
                "'" + id + "': undeclared identifier");
    }

    public static SyntaxErrorException redefinition(Token token, String id) {
        return new SyntaxErrorException(token,
                "'" + id + "': redefinition");
    }

    public static SyntaxErrorException notIntegerExpression(Token token) {
        return new SyntaxErrorException(token,
                "integer expression required");
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
                "l-values required (cannot modify a const)");
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

    public static SyntaxErrorException invalidSubscriptingType(Token token, String name) {
        return new SyntaxErrorException(token,
                "'" + name + "': left of '[' is not of type array, matrix or vector");
    }

    public static SyntaxErrorException invalidSelectionType(Token token, String name) {
        return new SyntaxErrorException(token,
                "'" + name + "': field selection requires structure, vector, or interface block on left hand side");
    }

    public static SyntaxErrorException cannotConvert(Token token, Type from, Type to) {
        return new SyntaxErrorException(token,
                "cannot convert from '" + from + "' to '" + to + "'");
    }

    public static SyntaxErrorException invalidMethod(Token token, String name) {
        return new SyntaxErrorException(token, "'" + name + "': invalid method");
    }

    public static SyntaxErrorException lengthOnlyArrays(Token token) {
        return new SyntaxErrorException(token, "length can only be called on arrays");
    }
}

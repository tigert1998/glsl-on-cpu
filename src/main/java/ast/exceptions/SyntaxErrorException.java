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

    public static SyntaxErrorException notBooleanExpression(Token token) {
        return new SyntaxErrorException(token, Messages.notBooleanExpression);
    }

    public static SyntaxErrorException arrayOfArrays(Token token) {
        return new SyntaxErrorException(token,
                "cannot declare array of arrays");
    }

    public static SyntaxErrorException structArrayMemberUnknownSize(Token token, String id) {
        return new SyntaxErrorException(token,
                "'" + id + "': array members of structs must specify a getN");
    }

    public static SyntaxErrorException lvalueRequired(Token token) {
        return new SyntaxErrorException(token, Messages.lvalueRequired);
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
                "array getN must be greater than zero");
    }

    public static SyntaxErrorException invalidSubscriptingType(Token token, Type type) {
        return new SyntaxErrorException(token, Messages.invalidSubscriptingType(type));
    }

    public static SyntaxErrorException invalidSelectionType(Token token, Type type) {
        return new SyntaxErrorException(token, Messages.invalidSelectionType(type));
    }

    public static SyntaxErrorException cannotConvert(Token token, Type from, Type to) {
        return new SyntaxErrorException(token, Messages.cannotConvert(from, to));
    }

    public static SyntaxErrorException invalidMethod(Token token, String name) {
        return new SyntaxErrorException(token, "'" + name + "': invalid method");
    }

    public static SyntaxErrorException lengthOnlyArrays(Token token) {
        return new SyntaxErrorException(token, "length can only be called on arrays");
    }

    public static SyntaxErrorException implicitSizedArray(Token token) {
        return new SyntaxErrorException(token, "implicit sized array need to be initialized");
    }

    public static SyntaxErrorException voidCannotReturnValue(Token token) {
        return new SyntaxErrorException(token, "'return': void function cannot return a value");
    }

    public static SyntaxErrorException returnNotMatch(Token token) {
        return new SyntaxErrorException(token, "'return': function return is not matching type");
    }

    public static SyntaxErrorException notReturnValue(Token token) {
        return new SyntaxErrorException(token, "'return': non-void function must return a value");
    }

    public static SyntaxErrorException notMatchFunction(Token token, String id) {
        return new SyntaxErrorException(token, "'" + id + "': no matching overloaded function found");
    }

    public static SyntaxErrorException invalidBreak(Token token) {
        return new SyntaxErrorException(token, "break statement only allowed in loops and switch statements");
    }

    public static SyntaxErrorException invalidContinue(Token token) {
        return new SyntaxErrorException(token, "continue statement only allowed in loops");
    }

    public static SyntaxErrorException switchInteger(Token token) {
        return new SyntaxErrorException(token, "'switch': init-expression" +
                " in a switch statement must be a scalar integer");
    }

    public static SyntaxErrorException caseLabelTypeMismatch(Token token) {
        return new SyntaxErrorException(token, "'case': case label type does not match switch init-expression type");
    }

    public static SyntaxErrorException constantExpressionRequired(Token token) {
        return new SyntaxErrorException(token, "constant expression required");
    }

    public static SyntaxErrorException duplicateCase(Token token) {
        return new SyntaxErrorException(token, "'" + token.getText() + "': duplicate case label");
    }
}

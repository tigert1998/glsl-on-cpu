package ast.exceptions;

public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(String message) {
        super(message);
    }

    public static SyntaxErrorException undeclaredID(String id) {
        return new SyntaxErrorException("'" + id + "': undeclared identifier");
    }

    public static SyntaxErrorException redefinition(String id) {
        return new SyntaxErrorException("'" + id + "': redefinition");
    }

    public static SyntaxErrorException invalidArraySizeType() {
        return new SyntaxErrorException("array size must be a constant integer expression");
    }

    public static SyntaxErrorException arrayOfArrays() {
        return new SyntaxErrorException("cannot declare array of arrays");
    }

    public static SyntaxErrorException structArrayMemberUnknownSize(String id) {
        return new SyntaxErrorException("'" + id + "': array members of structs must specify a size");
    }

    public static SyntaxErrorException lvalueRequired() {
        return new SyntaxErrorException("l-value required (cannot modify a const)");
    }

    public static SyntaxErrorException embeddedStructDefinition() {
        return new SyntaxErrorException("embedded struct definitions are not allowed");
    }

    public static SyntaxErrorException duplicateFieldName(String id) {
        return new SyntaxErrorException("'" + id + "': duplicate field name in structure");
    }
}

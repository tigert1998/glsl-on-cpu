package ast.exceptions;

import ast.types.*;

public class InvalidSelectionException extends UnlocatedSyntaxErrorException {
    private InvalidSelectionException(String message) {
        super(message);
    }

    static public InvalidSelectionException illegalVectorFieldSelection(String name) {
        return new InvalidSelectionException("'" + name + "': illegal vector field selection");
    }

    static public InvalidSelectionException outOfRange(String name) {
        return new InvalidSelectionException("'" + name + "': vector field selection out of range");
    }

    static public InvalidSelectionException notSameSet(String name) {
        return new InvalidSelectionException(
                "'" + name + "': illegal - vector component fields not from the same set");
    }

    static public InvalidSelectionException noSuchField(String name) {
        return new InvalidSelectionException("'" + name + "': no such field in structure");
    }

    public static InvalidSelectionException invalidSelectionType(Type type) {
        return new InvalidSelectionException(Messages.invalidSelectionType(type));
    }

    public static InvalidSelectionException invalidSwizzleType(Type type) {
        return new InvalidSelectionException(
                "'" + type + "': swizzle needs vector type");
    }
}

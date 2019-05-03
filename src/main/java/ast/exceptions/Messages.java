package ast.exceptions;

import ast.types.Type;

class Messages {
    static String invalidSubscriptingType(Type type) {
        return "'" + type + "': left of '[' is not of type array, matrix or vector";
    }

    static String notIntegerExpression = "integer expression required";

    static String notBooleanExpression = "boolean expression required";

    static String invalidSelectionType(Type type) {
        return "'" + type + "': field selection requires structure, vector, or interface block on left hand side";
    }

    static String cannotConvert(Type from, Type to) {
        return "cannot convert from '" + from + "' to '" + to + "'";
    }

    static String lvalueRequired = "l-values required (cannot modify a const)";
}

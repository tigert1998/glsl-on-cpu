package ast.exceptions;

import ast.FunctionSignature;

public class ScopeException extends UnlocatedSyntaxErrorException {
    private ScopeException(String message) {
        super(message);
    }

    public static ScopeException sameQualifier() {
        return new ScopeException("function must have the same parameter qualifier " +
                "and the same return type in all its declarations");
    }

    public static ScopeException alreadyBody(String id) {
        return new ScopeException("'" + id + "': function already has a body");
    }

    public static ScopeException functionRedefinition(String id) {
        return new ScopeException("'" + id + "': redefinition of a function");
    }

    public static ScopeException declarationCLinkage(FunctionSignature sig) {
        return new ScopeException("conflicting declaration of '" + sig.toString()
                + "' with 'C' linkage");
    }
}

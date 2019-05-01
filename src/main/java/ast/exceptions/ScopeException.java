package ast.exceptions;

public class ScopeException extends UnlocatedSyntaxErrorException {
    private ScopeException(String message) {
        super(message);
    }

    public static ScopeException sameQualifier() {
        return new ScopeException("function must have the same parameter qualifier in all its declarations");
    }

    public static ScopeException alreadyBody(String id) {
        return new ScopeException("'" + id + "': function already has a body");
    }

    public static ScopeException functionRedefinition(String id) {
        return new ScopeException("'" + id + "': redefinition of a function");
    }
}

package ast.expr;

import ast.types.Type;

public class ReferenceExpr extends Expr {
    private String id;

    public ReferenceExpr(Type type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}

package ast.expr;

import ast.types.Type;

public class ConstructionExpr extends Expr {
    private Type type;
    private Expr[] exprs;

    public ConstructionExpr(Type type, Expr[] exprs) {
        this.type = type;
        this.exprs = exprs;
    }
}

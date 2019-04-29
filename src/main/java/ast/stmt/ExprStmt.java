package ast.stmt;

import ast.expr.Expr;

public class ExprStmt extends Stmt {
    private Expr expr;

    public ExprStmt(Expr expr) {
        this.expr = expr;
    }
}

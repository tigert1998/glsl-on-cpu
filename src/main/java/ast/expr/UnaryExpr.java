package ast.expr;

import ast.operators.UnaryOperator;

public class UnaryExpr extends Expr {
    private UnaryOperator op;
    private Expr expr;

    public UnaryExpr(UnaryOperator op, Expr expr) {
        this.op = op;
        this.expr = expr;
        this.isLValue = false;
    }
}

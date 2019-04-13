package ast.expr;

import ast.operators.BinaryOperator;

public class BinaryExpr extends Expr {
    private BinaryOperator op;
    private Expr[] exprs;

    public BinaryExpr(BinaryOperator op, Expr[] exprs) {
        this.op = op;
        this.exprs = exprs;
    }
}

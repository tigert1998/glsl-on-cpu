package ast.expr;

import ast.exceptions.*;
import ast.operators.*;

public class BinaryExpr extends Expr {
    private BinaryOperator op;
    private Expr[] exprs;

    public BinaryExpr(BinaryOperator op, Expr[] exprs) {
        try {
            this.type = op.apply(exprs[0].getType(), exprs[1].getType());
        } catch (OperatorCannotBeAppliedException ignore) {}
        this.op = op;
        this.exprs = exprs;
    }

    @Override
    public String toString() {
        return exprs[0] + " " + op + " " + exprs[1];
    }
}

package ast.expr;

import ast.FunctionSignature;

public class CallExpr extends Expr {
    private FunctionSignature functionSignature;
    private Expr[] exprs;

    public CallExpr(FunctionSignature functionSignature, Expr[] exprs) {
        this.functionSignature = functionSignature;
        this.exprs = exprs;
    }
}

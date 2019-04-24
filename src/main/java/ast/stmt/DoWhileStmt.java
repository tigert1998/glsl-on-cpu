package ast.stmt;

import ast.expr.Expr;

public class DoWhileStmt extends Stmt {
    private Stmt stmt;
    private Expr condition;
}

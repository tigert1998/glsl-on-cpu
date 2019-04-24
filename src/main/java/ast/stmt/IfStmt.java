package ast.stmt;

import ast.expr.Expr;

public class IfStmt extends Stmt {
    private Expr condition;
    private Stmt ifStmt, elseStmt;
}

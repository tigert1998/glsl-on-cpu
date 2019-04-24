package ast.stmt;

import ast.expr.Expr;

public class ForStmt extends Stmt {
    private Stmt initialization;
    private Expr condition;
    private Stmt loop;
    private Stmt body;
}

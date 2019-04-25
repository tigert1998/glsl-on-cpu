package ast.stmt;

import ast.expr.Expr;
import ast.types.Type;

public class DeclarationStmt extends Stmt {
    private Type type;
    private String id;
    private Expr expr;

    public DeclarationStmt(Type type, String id, Expr expr) {
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    @Override
    public String toString() {
        String declaration = type + " " + id;
        if (expr == null) return declaration + ";";
        return declaration + " = " + expr + ";";
    }
}

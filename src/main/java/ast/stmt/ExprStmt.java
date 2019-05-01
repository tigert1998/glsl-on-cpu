package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class ExprStmt extends Stmt {
    private Expr expr;

    public ExprStmt(Expr expr) {
        this.expr = expr;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        return json;
    }
}

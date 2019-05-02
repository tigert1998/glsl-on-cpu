package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class ReturnStmt extends Stmt {
    public Expr expr;

    public ReturnStmt(Expr expr) {
        this.expr = expr;
    }

    public ReturnStmt() {
        this.expr = null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        return json;
    }
}

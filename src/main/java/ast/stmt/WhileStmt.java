package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class WhileStmt extends Stmt {
    public Expr condition;
    public Stmt body;

    public WhileStmt(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("condition", condition.toJSON());
        json.put("body", body.toJSON());
        return json;
    }
}

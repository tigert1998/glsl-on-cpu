package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class DoWhileStmt extends Stmt {
    public CompoundStmt body;
    public Expr condition;

    public DoWhileStmt(CompoundStmt body, Expr condition) {
        this.body = body;
        this.condition = condition;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("body", body.toJSON());
        json.put("condition", condition.toJSON());
        return json;
    }
}

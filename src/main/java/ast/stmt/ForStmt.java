package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class ForStmt extends Stmt {
    public CompoundStmt initialization;
    public Expr condition;
    public CompoundStmt step;
    public CompoundStmt body;

    public ForStmt(CompoundStmt initialization, Expr condition, CompoundStmt step, CompoundStmt body) {
        this.initialization = initialization;
        this.condition = condition;
        this.step = step;
        this.body = body;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("initialization", initialization.toJSON());
        json.put("condition", condition.toJSON());
        json.put("step", step.toJSON());
        json.put("body", body.toJSON());
        return json;
    }
}

package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class ForStmt extends Stmt {
    public Stmt initialization;
    public Expr condition;
    public Stmt step;
    public Stmt body;

    public ForStmt(Stmt initialization, Expr condition, Stmt step, Stmt body) {
        this.initialization = initialization;
        this.condition = condition;
        this.step = step;
        this.body = body;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        if (initialization != null)
            json.put("initialization", initialization.toJSON());
        if (condition != null)
            json.put("condition", condition.toJSON());
        if (step != null)
            json.put("step", step.toJSON());
        json.put("body", body.toJSON());
        return json;
    }
}

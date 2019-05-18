package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class WhileStmt extends Stmt {
    public Expr condition;
    public CompoundStmt body;
    private ControlFlowManager controlFlowManager;

    public WhileStmt(Expr condition, CompoundStmt body, ControlFlowManager controlFlowManager) {
        this.condition = condition;
        this.body = body;
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("condition", condition.toJSON());
        json.put("body", body.toJSON());
        return json;
    }
}

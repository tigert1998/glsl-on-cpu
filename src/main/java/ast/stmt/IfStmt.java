package ast.stmt;

import ast.expr.Expr;
import org.json.JSONObject;

public class IfStmt extends Stmt {
    public Expr condition;
    public CompoundStmt thenStmt, elseStmt;

    public IfStmt(Expr condition, CompoundStmt thenStmt, CompoundStmt elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("condition", condition.toJSON());
        json.put("then", thenStmt.toJSON());
        json.put("else", elseStmt.toJSON());
        return json;
    }
}

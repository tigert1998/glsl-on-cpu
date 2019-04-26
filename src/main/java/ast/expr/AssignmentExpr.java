package ast.expr;

import ast.operators.BinaryOperator;
import org.json.JSONObject;

public class AssignmentExpr extends Expr {
    private Expr x, y;
    BinaryOperator auxOp;

    // lvalue op y
    public AssignmentExpr(BinaryOperator auxOp, Expr x, Expr y) {
        this.isLValue = true;
        this.type = x.getType();
        this.x = x;
        this.auxOp = auxOp;
        this.y = y;
    }

    @Override
    protected JSONObject toJSON() {
        var json = super.toJSON();
        json.put("auxOp", auxOp);
        json.put("x", x.toJSON());
        json.put("y", y.toJSON());
        return json;
    }
}

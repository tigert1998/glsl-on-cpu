package ast.expr;

import ast.exceptions.*;
import ast.operators.*;

import org.json.*;

public class BinaryExpr extends Expr {
    private BinaryOperator op;
    private Expr[] exprs;

    private BinaryExpr(BinaryOperator op, Expr[] exprs) {
        try {
            this.type = op.apply(exprs[0].getType(), exprs[1].getType());
        } catch (OperatorCannotBeAppliedException ignore) {
        }
        this.op = op;
        this.exprs = exprs;
        this.isLValue = false;
    }

    public static Expr factory(BinaryOperator op, Expr[] exprs) throws ArithmeticException {
        if (exprs[0] instanceof ConstExpr && exprs[1] instanceof ConstExpr) {
            try {
                return new ConstExpr(op.apply(((ConstExpr) exprs[0]).getValue(), ((ConstExpr) exprs[1]).getValue()));
            } catch (OperatorCannotBeAppliedException ignore) {
                return null;
            }
        } else {
            return new BinaryExpr(op, exprs);
        }
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("op", op.toString());
        var exprsJson = new JSONArray();
        exprsJson.put(exprs[0].toJSON());
        exprsJson.put(exprs[1].toJSON());
        json.put("exprs", exprsJson);
        return json;
    }
}

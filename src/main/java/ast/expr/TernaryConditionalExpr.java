package ast.expr;

import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.json.JSONObject;

public class TernaryConditionalExpr extends Expr {
    private Expr judgement;
    private Expr x, y;

    private TernaryConditionalExpr(Expr judgement, Expr x, Expr y) {
        isLValue = x.isLValue && y.isLValue;
        this.judgement = judgement;
        this.type = x.getType();
        this.x = x;
        this.y = y;
    }

    static public Expr factory(Expr judgement, Expr x, Expr y) throws UnlocatedSyntaxErrorException {
        if (!(judgement.getType() instanceof BoolType))
            throw UnlocatedSyntaxErrorException.notBooleanExpression();
        if (!x.getType().equals(y.getType()))
            throw UnlocatedSyntaxErrorException.cannotConvert(y.getType(), x.getType());
        if (judgement instanceof ConstExpr) {
            return ((BoolValue) ((ConstExpr) judgement).getValue()).value ? x : y;
        }
        return new TernaryConditionalExpr(judgement, x, y);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("judgement", judgement.toJSON());
        json.put("x", x.toJSON());
        json.put("y", y.toJSON());
        return json;
    }
}

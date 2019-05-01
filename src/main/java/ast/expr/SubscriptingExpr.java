package ast.expr;

import ast.exceptions.InvalidIndexException;
import ast.values.*;
import org.json.JSONObject;

public class SubscriptingExpr extends Expr {
    private Expr x;
    private Expr index;

    private SubscriptingExpr(Expr x, Expr index) {
        isLValue = x.isLValue;
        this.type = x.getType().collapse();
        this.x = x;
        this.index = index;
    }

    static public Expr factory(Expr x, Expr index) {
        if (x instanceof ConstExpr && index instanceof ConstExpr) {
            int i = Value.evalAsIntegral(((ConstExpr) index).getValue());
            try {
                return new ConstExpr(((Indexed) ((ConstExpr) x).getValue()).valueAt(i));
            } catch (InvalidIndexException ignore) {
                return null;
            }
        }
        return new SubscriptingExpr(x, index);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("x", x.toJSON());
        json.put("index", index.toJSON());
        return json;
    }
}

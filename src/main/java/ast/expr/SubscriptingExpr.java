package ast.expr;

import ast.exceptions.InvalidIndexException;
import ast.values.Indexed;
import org.json.JSONObject;

public class SubscriptingExpr extends Expr {
    private Expr x;
    private Expr index;

    public SubscriptingExpr(Expr x, Expr index) {
        isLValue = x.isLValue;
        try {
            this.type = ((Indexed) x.getType().getDefaultValue()).valueAt(0).getType();
        } catch (InvalidIndexException ignore) {}
        this.x = x;
        this.index = index;
    }

    @Override
    protected JSONObject toJSON() {
        var json = super.toJSON();
        json.put("x", x.toJSON());
        json.put("index", index.toJSON());
        return json;
    }
}

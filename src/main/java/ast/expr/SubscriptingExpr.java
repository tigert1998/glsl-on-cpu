package ast.expr;

import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.json.JSONObject;

public class SubscriptingExpr extends Expr {
    private Expr x;
    private Expr index;

    private SubscriptingExpr(Expr x, Expr index) {
        isLValue = x.isLValue;
        this.type = ((IndexedType) x.getType()).elementType();
        this.x = x;
        this.index = index;
    }

    static public Expr factory(Expr x, Expr index) throws UnlocatedSyntaxErrorException {
        if (!(x.getType() instanceof IndexedType))
            throw InvalidIndexException.invalidSubscriptingType(x.getType());

        var type = (IndexedType) x.getType();

        if (index instanceof ConstExpr) {
            int i = Value.evalAsIntegral(((ConstExpr) index).getValue());
            if (i >= type.getN()) throw InvalidIndexException.outOfRange();
            if (x instanceof ConstExpr) {
                return new ConstExpr(((Indexed) ((ConstExpr) x).getValue()).valueAt(i));
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

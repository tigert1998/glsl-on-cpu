package ast.expr;

import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.json.JSONObject;

public class SelectionExpr extends Expr {
    public Expr expr;
    public String selection;

    private SelectionExpr(Expr expr, String selection) {
        isLValue = expr.isLValue;
        this.expr = expr;
        this.selection = selection;
        this.type = ((StructType) expr.getType()).getFieldInfo(selection).type;
    }

    public static Expr factory(Expr expr, String selection) throws InvalidSelectionException {
        if (expr instanceof ConstExpr) {
            StructValue value = (StructValue) ((ConstExpr) expr).getValue();
            return new ConstExpr(value.select(selection));
        } else {
            return new SelectionExpr(expr, selection);
        }
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        json.put("selection", selection);
        return json;
    }
}

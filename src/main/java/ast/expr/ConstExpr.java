package ast.expr;

import ast.values.*;

import org.json.*;

public class ConstExpr extends Expr {
    private Value value;

    public ConstExpr(Value value) {
        this.value = value;
        this.type = value.getType();
        this.isLValue = false;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("value", value.toString());
        return json;
    }
}

package ast.expr;

import ast.AST;
import ast.types.Type;

import org.json.*;

public abstract class Expr extends AST {
    protected Type type = null;
    protected boolean isLValue = false;

    public Type getType() {
        return type;
    }

    public boolean isLValue() {
        return isLValue;
    }

    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("type", getType().toString());
        json.put("isLValue", isLValue);
        return json;
    }
}

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

    protected JSONObject toJSON() {
        var json = new JSONObject();
        json.put("class", getClass().getSimpleName());
        json.put("type", getType().toString());
        json.put("isLValue", isLValue);
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString(4);
    }
}

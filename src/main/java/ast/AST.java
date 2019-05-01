package ast;

import org.json.JSONObject;

public abstract class AST {
    public JSONObject toJSON() {
        var json = new JSONObject();
        json.put("class", getClass().getSimpleName());
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString(4);
    }
}

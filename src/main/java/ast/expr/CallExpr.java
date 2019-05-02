package ast.expr;

import ast.FunctionSignature;
import org.json.*;

public class CallExpr extends Expr {
    public FunctionSignature functionSignature;
    public Expr[] exprs;

    public CallExpr(FunctionSignature functionSignature, Expr[] exprs) {
        this.functionSignature = functionSignature;
        this.exprs = exprs;
        this.type = functionSignature.returnType;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("signature", functionSignature.toString());
        var array = new JSONArray();
        for (var expr : exprs) array.put(expr.toJSON());
        json.put("exprs", array);
        return json;
    }
}

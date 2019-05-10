package ast.expr;

import ast.Scope;
import ast.types.*;
import ast.values.*;
import ast.exceptions.*;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.json.*;

public class ConstructionExpr extends Expr {
    private Expr[] exprs;

    private ConstructionExpr(Type type, Expr[] exprs) {
        this.type = type;
        this.exprs = exprs;
        this.isLValue = false;
    }

    public static Expr factory(Type type, Expr[] exprs) throws ConstructionFailedException {
        // check syntax
        type.construct(exprs);

        var values = new Value[exprs.length];
        for (int i = 0; i < exprs.length; i++) {
            var expr = exprs[i];
            if (!(expr instanceof ConstExpr)) return new ConstructionExpr(type, exprs);
            values[i] = ((ConstExpr) expr).getValue();
        }
        return new ConstExpr(type.construct(values));
    }

    @Override
    public LLVMValueRef evaluate(LLVMValueRef function, Scope scope) {
        return this.type.construct(exprs, function, scope);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for (var expr : exprs) array.put(expr.toJSON());
        json.put("exprs", array);
        return json;
    }
}

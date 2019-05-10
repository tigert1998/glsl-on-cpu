package ast.expr;

import ast.AST;
import ast.Scope;
import ast.types.Type;

import org.bytedeco.llvm.LLVM.LLVMValueRef;
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

    // FIXME: should be abstract
    public LLVMValueRef evaluate(LLVMValueRef function, Scope scope) {
        return null;
    }

    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("type", getType().toString());
        json.put("isLValue", isLValue);
        return json;
    }
}

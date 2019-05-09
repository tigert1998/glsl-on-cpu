package ast.expr;

import ast.Scope;
import ast.values.*;

import org.bytedeco.llvm.LLVM.LLVMValueRef;
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
    public LLVMValueRef evaluate(LLVMValueRef function, Scope scope) {
        return value.ptrInLLVM(function);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("value", value.toString());
        return json;
    }
}

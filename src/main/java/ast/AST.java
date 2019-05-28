package ast;

import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

public abstract class AST {
    public JSONObject toJSON() {
        var json = new JSONObject();
        json.put("class", getClass().getSimpleName());
        return json;
    }

    abstract public LLVMValueRef evaluate(
            LLVMModuleRef module, LLVMValueRef function, Scope scope);

    @Override
    public String toString() {
        return toJSON().toString(2);
    }
}

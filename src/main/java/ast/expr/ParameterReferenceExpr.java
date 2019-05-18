package ast.expr;

import ast.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;

public class ParameterReferenceExpr extends Expr {
    public FunctionSignature.ParameterInfo parameterInfo;

    public ParameterReferenceExpr(FunctionSignature.ParameterInfo parameterInfo) {
        this.parameterInfo = parameterInfo;
        this.type = parameterInfo.type;
        this.isLValue = (parameterInfo.qualifier != FunctionSignature.ParameterQualifier.CONST_IN);
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        return LLVMGetParam(function, parameterInfo.index);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("id", parameterInfo.id);
        return json;
    }
}

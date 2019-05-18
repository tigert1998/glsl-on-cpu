package ast.expr;

import ast.*;
import ast.types.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class CallExpr extends Expr {
    public FunctionSignature functionSignature;
    public Expr[] exprs;

    // types will always match because every time function signatures are searched to match it
    // therefore no need for throw exception
    public CallExpr(FunctionSignature functionSignature, Expr[] exprs) {
        this.functionSignature = functionSignature;
        this.exprs = exprs;
        this.type = functionSignature.returnType;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var toBeCalled = scope.lookupLLVMFunction(functionSignature, module);

        LLVMValueRef result = null;

        var values = new LLVMValueRef[exprs.length + (functionSignature.returnType instanceof VoidType ? 0 : 1)];
        for (int i = 0; i < exprs.length; i++) {
            var value = exprs[i].evaluate(module, function, scope);
            if (functionSignature.parameters.get(i).qualifier == FunctionSignature.ParameterQualifier.IN) {
                values[i] = deepCopy(exprs[i].getType(), function, value);
            } else {
                values[i] = value;
            }
        }

        if (!(functionSignature.returnType instanceof VoidType)) {
            var tmp = buildAllocaInFirstBlock(function, functionSignature.returnType.inLLVM(), "");
            result = loadPtr(functionSignature.returnType, function, tmp);
            values[values.length - 1] = result;
        }

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        LLVMBuildCall(builder, toBeCalled, new PointerPointer<>(values), values.length, "");
        LLVMDisposeBuilder(builder);
        return result;
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

package ast;

import ast.stmt.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;

public class FunctionAST extends AST {
    private FunctionSignature functionSignature;
    private CompoundStmt stmt;

    public FunctionAST(FunctionSignature functionSignature, CompoundStmt stmt) {
        this.functionSignature = functionSignature;
        this.stmt = stmt;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var func = scope.lookupLLVMFunction(functionSignature, module);
        var init = LLVMAppendBasicBlock(func, "init");
        var entry = LLVMAppendBasicBlock(func, "entry");
        stmt.evaluate(module, func, scope);

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, init);
        LLVMBuildBr(builder, entry);
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(func));
        LLVMBuildRetVoid(builder);

        LLVMDisposeBuilder(builder);
        return func;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("signature", functionSignature.toString());
        json.put("compoundStmt", stmt.toJSON());
        return json;
    }
}

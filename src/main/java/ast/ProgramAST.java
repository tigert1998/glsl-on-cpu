package ast;

import ast.stmt.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class ProgramAST extends AST {
    private List<DeclarationStmt> declarationStmts = new ArrayList<>();
    private List<FunctionAST> functions = new ArrayList<>();

    public void putDeclarationStmt(DeclarationStmt stmt) {
        declarationStmts.add(stmt);
    }

    public void putFunctionAST(FunctionAST ast) {
        functions.add(ast);
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        for (var kv : scope.innerScopes.peek().variables.entrySet()) {
            var stmt = kv.getValue();
            var value = LLVMAddGlobal(module, stmt.type.inLLVM(), kv.getKey());
            LLVMSetInitializer(value, stmt.type.zero().inLLVM());
            stmt.setLLVMValue(value);
        }
        buildGlobalVarInit(module, scope);
        for (var func : functions) {
            func.evaluate(module, null, scope);
        }
        return null;
    }

    private void buildGlobalVarInit(LLVMModuleRef module, Scope scope) {
        var func = LLVMAddFunction(module, ".global_var_init",
                LLVMFunctionType(LLVMVoidType(), LLVMVoidType(), 0, 0));
        var init = LLVMAppendBasicBlock(func, "init");
        var entry = LLVMAppendBasicBlock(func, "entry");

        for (var decl : declarationStmts) {
            var llvmValue = decl.expr.evaluate(module, func, scope);
            decl.storeLLVMValue(func, llvmValue);
        }

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, init);
        LLVMBuildBr(builder, entry);

        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(func));
        LLVMBuildRetVoid(builder);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var funcArray = new JSONArray();
        functions.forEach(function -> funcArray.put(function.toJSON()));
        json.put("functions", funcArray);
        var declArray = new JSONArray();
        declarationStmts.forEach(decl -> declArray.put(decl.toJSON()));
        json.put("declarationStmts", declArray);
        return json;
    }
}

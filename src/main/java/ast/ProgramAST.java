package ast;

import ast.expr.ConstExpr;
import ast.stmt.*;
import codegen.LLVMUtility;
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
            LLVMSetInitializer(value, ((ConstExpr) stmt.expr).getValue().inLLVM());
            stmt.setLLVMValue(value);
        }
        for (var func : functions) {
            func.evaluate(module, null, scope);
        }
        return null;
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

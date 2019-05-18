package ast.stmt;

import ast.*;
import ast.expr.Expr;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class ReturnStmt extends Stmt {
    public Expr expr;

    public ReturnStmt(Expr expr) {
        this.expr = expr;
    }

    public ReturnStmt() {
        this.expr = null;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        if (expr != null) {
            var yptr = expr.evaluate(module, function, scope);
            var xptr = LLVMGetLastParam(function);
            assign(expr.getType(), function, xptr, yptr);
        }
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        LLVMBuildRetVoid(builder);
        LLVMDisposeBuilder(builder);
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        if (expr != null) json.put("expr", expr.toJSON());
        return json;
    }
}

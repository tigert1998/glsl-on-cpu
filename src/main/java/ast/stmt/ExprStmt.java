package ast.stmt;

import ast.Scope;
import ast.expr.Expr;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

public class ExprStmt extends Stmt {
    private Expr expr;

    public ExprStmt(Expr expr) {
        this.expr = expr;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        expr.evaluate(module, function, scope);
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        return json;
    }
}

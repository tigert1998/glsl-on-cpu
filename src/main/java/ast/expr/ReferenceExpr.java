package ast.expr;

import ast.Scope;
import ast.stmt.DeclarationStmt;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.json.JSONObject;

public class ReferenceExpr extends Expr {
    private DeclarationStmt declarationStmt;

    public ReferenceExpr(DeclarationStmt declarationStmt) {
        this.declarationStmt = declarationStmt;
        isLValue = true;
        this.type = declarationStmt.type;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("id", declarationStmt.id);
        return json;
    }

    @Override
    public LLVMValueRef evaluate(LLVMValueRef function, Scope scope) {
        return declarationStmt.loadLLVMValue(function);
    }
}

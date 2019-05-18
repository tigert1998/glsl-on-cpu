package ast.expr;

import ast.Scope;
import ast.exceptions.*;
import ast.types.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class AssignmentExpr extends Expr {
    private Expr x, y;

    // x = y
    public AssignmentExpr(Expr x, Expr y) throws UnlocatedSyntaxErrorException {
        if (!x.isLValue())
            throw UnlocatedSyntaxErrorException.lvalueRequired();
        if (x.getType() != y.getType())
            throw UnlocatedSyntaxErrorException.cannotConvert(y.getType(), x.getType());
        this.isLValue = true;
        this.type = x.getType();
        this.x = x;
        this.y = y;
    }

    // e* = e*
    // [n x e*]* = [n x e*]*
    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var yptr = y.evaluate(module, function, scope);
        var xptr = x.evaluate(module, function, scope);
        assign(x.getType(), function, xptr, yptr);
        return xptr;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("x", x.toJSON());
        json.put("y", y.toJSON());
        return json;
    }
}

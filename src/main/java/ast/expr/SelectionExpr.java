package ast.expr;

import ast.Scope;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class SelectionExpr extends Expr {
    public Expr expr;
    public String selection;

    private SelectionExpr(Expr expr, String selection) throws InvalidSelectionException {
        isLValue = expr.isLValue;
        this.expr = expr;
        this.selection = selection;
        this.type = ((StructType) expr.getType()).getFieldInfo(selection).type;
        if (this.type == null)
            throw InvalidSelectionException.noSuchField(selection);
    }

    public static Expr factory(Expr expr, String selection) throws InvalidSelectionException {
        if (!(expr.getType() instanceof StructType))
            throw InvalidSelectionException.invalidSelectionType(expr.getType());
        if (expr instanceof ConstExpr) {
            StructValue value = (StructValue) ((ConstExpr) expr).getValue();
            return new ConstExpr(value.select(selection));
        } else {
            return new SelectionExpr(expr, selection);
        }
    }

    @Override
    public LLVMValueRef evaluate(LLVMValueRef function, Scope scope) {
        var value = expr.evaluate(function, scope);
        int idx = ((StructType) expr.getType()).getFieldInfoIndex(selection);

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        var result = buildGEP(builder, value, "", 0, idx);
        return preloadPtrValue(this.type, function, result);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        json.put("selection", selection);
        return json;
    }
}

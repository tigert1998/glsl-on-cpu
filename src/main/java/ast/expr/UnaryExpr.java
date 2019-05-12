package ast.expr;

import ast.Scope;
import ast.exceptions.*;
import ast.operators.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

public class UnaryExpr extends Expr {
    public UnaryOperator op;
    public Expr expr;

    private UnaryExpr(UnaryOperator op, Expr expr) throws OperatorCannotBeAppliedException {
        this.op = op;
        this.expr = expr;
        this.isLValue = false;
        this.type = op.apply(expr.getType());
    }

    static public Expr factory(UnaryOperator op, Expr expr) throws OperatorCannotBeAppliedException {
        if (expr instanceof ConstExpr) {
            var value = ((ConstExpr) expr).getValue();
            return new ConstExpr(op.apply(value));
        } else {
            return new UnaryExpr(op, expr);
        }
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        return op.apply(
                expr.getType(),
                expr.evaluate(module, function, scope), module, function, scope);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("op", op.toString());
        json.put("expr", expr.toJSON());
        return json;
    }
}

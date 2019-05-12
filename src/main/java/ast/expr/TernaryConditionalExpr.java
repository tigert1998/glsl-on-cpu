package ast.expr;

import ast.Scope;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class TernaryConditionalExpr extends Expr {
    private Expr judgement;
    private Expr x, y;

    private TernaryConditionalExpr(Expr judgement, Expr x, Expr y) {
        isLValue = x.isLValue && y.isLValue;
        this.judgement = judgement;
        this.type = x.getType();
        this.x = x;
        this.y = y;
    }

    static public Expr factory(Expr judgement, Expr x, Expr y) throws UnlocatedSyntaxErrorException {
        if (!(judgement.getType() instanceof BoolType))
            throw UnlocatedSyntaxErrorException.notBooleanExpression();
        if (!x.getType().equals(y.getType()))
            throw UnlocatedSyntaxErrorException.cannotConvert(y.getType(), x.getType());
        if (judgement instanceof ConstExpr) {
            return ((BoolValue) ((ConstExpr) judgement).getValue()).value ? x : y;
        }
        return new TernaryConditionalExpr(judgement, x, y);
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var xptr = x.evaluate(module, function, scope);
        var yptr = y.evaluate(module, function, scope);

        var result = buildAllocaInFirstBlock(function, this.type.withInnerPtrInLLVM(), "");

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        var judge = LLVMBuildLoad(builder, judgement.evaluate(module, function, scope), "");
        judge = LLVMBuildIntCast2(builder, judge, LLVMInt1Type(), 0, "");

        var blockX = LLVMAppendBasicBlock(function, "ternary_cond_x");
        var blockY = LLVMAppendBasicBlock(function, "ternary_cond_y");
        var end = LLVMAppendBasicBlock(function, "ternary_cond_end");

        LLVMBuildCondBr(builder, judge, blockX, blockY);

        LLVMPositionBuilderAtEnd(builder, blockX);
        LLVMBuildStore(builder, LLVMBuildLoad(builder, xptr, ""), result);
        LLVMBuildBr(builder, end);

        LLVMPositionBuilderAtEnd(builder, blockY);
        LLVMBuildStore(builder, LLVMBuildLoad(builder, yptr, ""), result);
        LLVMBuildBr(builder, end);
        return result;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("judgement", judgement.toJSON());
        json.put("x", x.toJSON());
        json.put("y", y.toJSON());
        return json;
    }
}

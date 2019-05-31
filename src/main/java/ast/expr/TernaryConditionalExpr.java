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
        var result = buildAllocaInFirstBlock(function, this.type.withInnerPtrInLLVM(), "");

        var judgementEvaluated = judgement.evaluate(module, function, scope);
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        var judge = buildCastBoolToInt1(builder, judgementEvaluated);

        var blockX = LLVMAppendBasicBlock(function, "ternary_cond_x");
        LLVMPositionBuilderAtEnd(builder, blockX);
        var xptr = x.evaluate(module, function, scope);
        LLVMBuildStore(builder, LLVMBuildLoad(builder, xptr, ""), result);

        var blockY = LLVMAppendBasicBlock(function, "ternary_cond_y");
        LLVMPositionBuilderAtEnd(builder, blockY);
        var yptr = y.evaluate(module, function, scope);
        LLVMBuildStore(builder, LLVMBuildLoad(builder, yptr, ""), result);

        var end = LLVMAppendBasicBlock(function, "ternary_cond_end");

        LLVMPositionBuilderAtEnd(builder, blockX);
        LLVMBuildBr(builder, end);
        LLVMPositionBuilderAtEnd(builder, blockY);
        LLVMBuildBr(builder, end);

        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(blockX));
        LLVMBuildCondBr(builder, judge, blockX, blockY);

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

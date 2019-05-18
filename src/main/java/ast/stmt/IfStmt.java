package ast.stmt;

import ast.Scope;
import ast.expr.Expr;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;

public class IfStmt extends Stmt {
    public Expr condition;
    public CompoundStmt thenStmt, elseStmt;

    public IfStmt(Expr condition, CompoundStmt thenStmt, CompoundStmt elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var lastBlock = LLVMGetLastBasicBlock(function);

        var conditionBlock = LLVMAppendBasicBlock(function, "condition");
        var value = condition.evaluate(module, function, scope);

        var thenBlock = LLVMAppendBasicBlock(function, "then");
        thenStmt.evaluate(module, function, scope);

        var elseBlock = LLVMAppendBasicBlock(function, "else");
        elseStmt.evaluate(module, function, scope);

        var endBlock = LLVMAppendBasicBlock(function, "end");

        // last condition then else end
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildBr(builder, conditionBlock);

        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(thenBlock));
        value = LLVMBuildIntCast2(builder, LLVMBuildLoad(builder, value, ""), LLVMInt1Type(), 0, "");
        LLVMBuildCondBr(builder, value, thenBlock, elseBlock);

        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(elseBlock));
        LLVMBuildBr(builder, endBlock);

        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(endBlock));
        LLVMBuildBr(builder, endBlock);
        LLVMDisposeBuilder(builder);
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("condition", condition.toJSON());
        json.put("then", thenStmt.toJSON());
        json.put("else", elseStmt.toJSON());
        return json;
    }
}

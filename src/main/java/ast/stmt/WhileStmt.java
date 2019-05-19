package ast.stmt;

import ast.Scope;
import ast.expr.Expr;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;

public class WhileStmt extends Stmt implements LoopStmt {
    public Expr condition;
    public CompoundStmt body;
    private ControlFlowManager controlFlowManager;

    public WhileStmt(Expr condition, CompoundStmt body, ControlFlowManager controlFlowManager) {
        this.condition = condition;
        this.body = body;
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var lastBlock = LLVMGetLastBasicBlock(function);

        var condBlock = LLVMAppendBasicBlock(function, "cond");
        var value = condition.evaluate(module, function, scope);

        var bodyBlock = LLVMAppendBasicBlock(function, "body");
        body.evaluate(module, function, scope);

        var endBlock = LLVMAppendBasicBlock(function, "end");

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildBr(builder, condBlock);

        // cond
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(bodyBlock));
        value = LLVMBuildLoad(builder, value, "");
        value = LLVMBuildIntCast2(builder, value, LLVMInt1Type(), 0, "");
        LLVMBuildCondBr(builder, value, bodyBlock, endBlock);

        // body
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(endBlock));
        LLVMBuildBr(builder, condBlock);

        LLVMDisposeBuilder(builder);

        LoopStmt.super.addContinueBreaks(controlFlowManager, condBlock, endBlock);
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("condition", condition.toJSON());
        json.put("body", body.toJSON());
        return json;
    }
}

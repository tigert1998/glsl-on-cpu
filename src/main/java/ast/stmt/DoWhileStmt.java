package ast.stmt;

import ast.Scope;
import ast.expr.Expr;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static codegen.LLVMUtility.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class DoWhileStmt extends Stmt {
    public CompoundStmt body;
    public Expr condition;
    private ControlFlowManager controlFlowManager;

    public DoWhileStmt(CompoundStmt body, Expr condition, ControlFlowManager controlFlowManager) {
        this.body = body;
        this.condition = condition;
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var lastBlock = LLVMGetLastBasicBlock(function);

        var bodyBlock = LLVMAppendBasicBlock(function, "body");
        body.evaluate(module, function, scope);

        var condBlock = LLVMAppendBasicBlock(function, "cond");
        var value = condition.evaluate(module, function, scope);

        var endBlock = LLVMAppendBasicBlock(function, "end");

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildBr(builder, bodyBlock);

        // body
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(condBlock));
        LLVMBuildBr(builder, condBlock);

        // cond
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(endBlock));
        value = buildCastBoolToInt1(builder, value);
        LLVMBuildCondBr(builder, value, bodyBlock, endBlock);

        LLVMDisposeBuilder(builder);

        controlFlowManager.evaluate(condBlock, endBlock);
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("body", body.toJSON());
        json.put("condition", condition.toJSON());
        return json;
    }
}

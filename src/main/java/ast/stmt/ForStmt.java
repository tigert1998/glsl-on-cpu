package ast.stmt;

import ast.Scope;
import ast.expr.Expr;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class ForStmt extends Stmt {
    public CompoundStmt initialization;
    public Expr condition;
    public CompoundStmt step;
    public CompoundStmt body;
    private ControlFlowManager controlFlowManager;

    public ForStmt(CompoundStmt initialization, Expr condition, CompoundStmt step, CompoundStmt body,
                   ControlFlowManager controlFlowManager) {
        this.initialization = initialization;
        this.condition = condition;
        this.step = step;
        this.body = body;
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var lastBlock = LLVMGetLastBasicBlock(function);

        var initBlock = LLVMAppendBasicBlock(function, "for.init");
        initialization.evaluate(module, function, scope);

        var condBlock = LLVMAppendBasicBlock(function, "for.cond");
        var value = condition.evaluate(module, function, scope);

        var stepBlock = LLVMAppendBasicBlock(function, "for.step");
        step.evaluate(module, function, scope);

        var bodyBlock = LLVMAppendBasicBlock(function, "for.body");
        body.evaluate(module, function, scope);

        var endBlock = LLVMAppendBasicBlock(function, "for.end");

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildBr(builder, initBlock);

        // init
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(condBlock));
        LLVMBuildBr(builder, condBlock);

        // cond
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(stepBlock));
        value = buildCastBoolToInt1(builder, value);
        LLVMBuildCondBr(builder, value, bodyBlock, endBlock);

        // step
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(bodyBlock));
        LLVMBuildBr(builder, condBlock);

        // body
        LLVMPositionBuilderAtEnd(builder, LLVMGetPreviousBasicBlock(endBlock));
        LLVMBuildBr(builder, stepBlock);

        LLVMDisposeBuilder(builder);

        controlFlowManager.evaluate(stepBlock, endBlock);
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("initialization", initialization.toJSON());
        json.put("condition", condition.toJSON());
        json.put("step", step.toJSON());
        json.put("body", body.toJSON());
        return json;
    }
}

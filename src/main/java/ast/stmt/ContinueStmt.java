package ast.stmt;

import ast.Scope;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;

public class ContinueStmt extends Stmt {
    private ControlFlowManager controlFlowManager;

    public ContinueStmt(ControlFlowManager controlFlowManager) {
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("controlFlowManager", controlFlowManager.toString());
        return json;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        controlFlowManager.addContinue(LLVMGetLastBasicBlock(function));
        return null;
    }
}

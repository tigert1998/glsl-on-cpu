package ast.stmt;

import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public interface LoopStmt {
    default void addContinueBreaks(ControlFlowManager manager, LLVMBasicBlockRef step, LLVMBasicBlockRef end) {
        var builder = LLVMCreateBuilder();

        for (int i = manager.controls.size() - 1; i >= 0; i--) {
            var control = manager.controls.get(i);
            var instr = control.instruction != null ?
                    LLVMGetNextInstruction(control.instruction) :
                    LLVMGetFirstInstruction(control.block);
            if (instr == null) {
                LLVMPositionBuilderAtEnd(builder, control.block);
            } else {
                LLVMPositionBuilderBefore(builder, instr);
            }
            if (control.type == ControlFlowManager.InstructionLocation.ControlType.CONTINUE) {
                LLVMBuildBr(builder, step);
            } else {
                LLVMBuildBr(builder, end);
            }
        }

        LLVMDisposeBuilder(builder);
    }
}

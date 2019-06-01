package ast.stmt;

import org.bytedeco.llvm.LLVM.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class ControlFlowManager {
    static public class InstructionLocation {
        public enum ControlType {
            CONTINUE, BREAK
        }

        public ControlType type;
        public LLVMValueRef instruction;
        public LLVMBasicBlockRef block;

        private InstructionLocation(ControlType type, LLVMValueRef instruction, LLVMBasicBlockRef block) {
            this.type = type;
            this.instruction = instruction;
            this.block = block;
        }
    }

    public List<InstructionLocation> controls = new ArrayList<>();

    public boolean allowContinue = true, allowBreak = true;

    public ControlFlowManager() {
    }

    public ControlFlowManager(boolean allowContinue, boolean allowBreak) {
        this.allowContinue = allowContinue;
        this.allowBreak = allowBreak;
    }

    public void addContinue(LLVMBasicBlockRef block) {
        controls.add(new InstructionLocation(InstructionLocation.ControlType.CONTINUE,
                LLVMGetLastInstruction(block), block));
    }

    public void addBreak(LLVMBasicBlockRef block) {
        controls.add(new InstructionLocation(InstructionLocation.ControlType.BREAK,
                LLVMGetLastInstruction(block), block));
    }

    public void evaluate(LLVMBasicBlockRef step, LLVMBasicBlockRef end) {
        var builder = LLVMCreateBuilder();

        for (int i = controls.size() - 1; i >= 0; i--) {
            var control = controls.get(i);
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

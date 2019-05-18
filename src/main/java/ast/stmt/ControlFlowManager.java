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

    public void addContinue(LLVMBasicBlockRef block) {
        controls.add(new InstructionLocation(InstructionLocation.ControlType.CONTINUE,
                LLVMGetLastInstruction(block), block));
    }

    public void addBreak(LLVMBasicBlockRef block) {
        controls.add(new InstructionLocation(InstructionLocation.ControlType.BREAK,
                LLVMGetLastInstruction(block), block));
    }
}

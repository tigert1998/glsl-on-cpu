package codegen;

import ast.*;

import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;
import org.bytedeco.llvm.global.LLVM;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class CodeGenerator {
    private LLVMModuleRef module;

    private void deadCodeElimination(LLVMModuleRef module) {
        for (var function = LLVMGetFirstFunction(module); function != null; function = LLVMGetNextFunction(function)) {
            if (LLVMIsDeclaration(function) != 0) continue;
            for (var block = LLVMGetFirstBasicBlock(function); block != null; block = LLVMGetNextBasicBlock(block)) {
                boolean terminated = false;
                var toBeDeleted = new ArrayList<LLVMValueRef>();
                for (var instr = LLVMGetFirstInstruction(block); instr != null; instr = LLVMGetNextInstruction(instr)) {
                    if (terminated) {
                        toBeDeleted.add(instr);
                    } else {
                        terminated = isTerminal(instr);
                    }
                }
                toBeDeleted.forEach(LLVM::LLVMInstructionRemoveFromParent);
            }
        }
    }

    public CodeGenerator(String name, Scope scope, ProgramAST program) {
        module = LLVMModuleCreateWithName(name);
        program.evaluate(module, null, scope);
        deadCodeElimination(module);
    }

    public void dump(String path) {
        BytePointer msg = new BytePointer((Pointer) null);
        LLVMVerifyModule(module, LLVMAbortProcessAction, msg);
        System.out.println(msg.getString());
        LLVMDisposeMessage(msg);
        LLVMWriteBitcodeToFile(module, path);
    }
}

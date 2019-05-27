package codegen;

import ast.*;

import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;
import org.bytedeco.llvm.global.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class CodeGenerator {
    private static LLVMModuleRef libModule;
    private static LLVMContextRef context = LLVMGetGlobalContext();
    private LLVMModuleRef module;

    {
        LLVMInitializeNativeTarget();
        libModule = LLVMModuleCreateWithName("lib");
        try {
            var uri = getClass().getClassLoader().getResource("glsl-on-cpu-llvm-lib").toURI();
            var dir = new File(uri);
            var pathStream = Files.walk(dir.toPath());
            var matcher = FileSystems.getDefault().getPathMatcher("glob:*.ll");
            pathStream.filter(path ->
                    matcher.matches(path.getFileName())).forEach(path -> {
                try {
                    byte[] content = Files.readAllBytes(path);
                    var module = compileIR(path.getFileName().toString(), content);
                    LLVMLinkModules2(libModule, module);
                } catch (IOException ignore) {
                }
            });
        } catch (URISyntaxException | IOException ignore) {
        }
    }

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
        module = LLVMModuleCreateWithNameInContext(name, context);
        program.evaluate(module, null, scope);
        deadCodeElimination(module);
    }

    public void dump(String path) {
        LLVMLinkModules2(module, libModule);
        BytePointer msg = new BytePointer((Pointer) null);
        LLVMVerifyModule(module, LLVMAbortProcessAction, msg);
        System.out.println(msg.getString());
        LLVMDisposeMessage(msg);
        LLVMWriteBitcodeToFile(module, path);
    }

    private static LLVMModuleRef compileIR(String id, byte[] content) {
        var mem = LLVMCreateMemoryBufferWithMemoryRangeCopy(
                new BytePointer(content), content.length, new BytePointer(""));
        LLVMModuleRef res = LLVMModuleCreateWithName(id);
        var msg = new BytePointer(1 << 10);
        LLVMParseIRInContext(context, mem, res, msg);
        LLVMVerifyModule(res, LLVMAbortProcessAction, msg);
        msg.deallocate();
        return res;
    }
}

package codegen;

import ast.Scope;

import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class CodeGenerator {
    private Scope scope;

    private LLVMModuleRef module;

    public CodeGenerator(String name, Scope scope) {
        this.scope = scope;

        module = LLVMModuleCreateWithName(name);
        for (var kv : scope.innerScopes.peek().variables.entrySet()) {
            var stmt = kv.getValue();
            var value = LLVMAddGlobal(module, stmt.type.inLLVM(), kv.getKey());
            LLVMSetInitializer(value, stmt.type.zero().inLLVM());
            stmt.setLLVMValue(value);
        }
        buildGlobalVarInit();
    }

    public void dump(String path) {
        var outMessage = new BytePointer((Pointer) null);
        LLVMVerifyModule(module, LLVMAbortProcessAction, outMessage);
        System.err.println(outMessage.getString());
        LLVMWriteBitcodeToFile(module, path);
    }

    private void buildGlobalVarInit() {
        var func = LLVMAddFunction(module, ".global_var_init",
                LLVMFunctionType(LLVMVoidType(), LLVMVoidType(), 0, 0));
        var init = LLVMAppendBasicBlock(func, "init");
        var entry = LLVMAppendBasicBlock(func, "entry");

        for (var kv : scope.innerScopes.peek().variables.entrySet()) {
            var llvmValue = kv.getValue().expr.evaluate(func, scope);
            kv.getValue().storeLLVMValue(func, llvmValue);
        }

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, init);
        LLVMBuildBr(builder, entry);

        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(func));
        LLVMBuildRetVoid(builder);
    }
}

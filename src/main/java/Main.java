import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import java.awt.*;
import java.nio.charset.StandardCharsets;

import static org.bytedeco.llvm.global.LLVM.*;

public class Main {
    public static void error(String message) {
        System.err.println(message);
        System.exit(0);
    }

    private static void testLLVM() {
        var mod = LLVMModuleCreateWithName("fib");
        var builder = LLVMCreateBuilder();

        var printd = LLVMAddFunction(mod,"printd",
                LLVMFunctionType(LLVMVoidType(), LLVMInt32Type(), 1, 0));
        LLVMSetLinkage(printd, LLVMExternalLinkage);

        var fib = LLVMAddFunction(mod, "fib",
                LLVMFunctionType(LLVMInt32Type(), LLVMInt32Type(), 1, 0));

        var entry = LLVMAppendBasicBlock(fib, "entry");
        LLVMPositionBuilderAtEnd(builder, entry);
        var a = LLVMBuildAlloca(builder, LLVMInt32Type(), "a");
        var b = LLVMBuildAlloca(builder, LLVMInt32Type(), "b");
        var i = LLVMBuildAlloca(builder, LLVMInt32Type(), "i");
        LLVMBuildStore(builder, LLVMConstInt(LLVMInt32Type(), 1, 0), a);
        LLVMBuildStore(builder, LLVMConstInt(LLVMInt32Type(), 1, 0), b);
        LLVMBuildStore(builder, LLVMConstInt(LLVMInt32Type(), 1, 0), i);
        var forCond = LLVMAppendBasicBlock(fib, "for.cond");
        var forBody = LLVMAppendBasicBlock(fib, "for.body");
        var forStep = LLVMAppendBasicBlock(fib, "for.step");
        var forEnd = LLVMAppendBasicBlock(fib, "for.end");
        LLVMBuildBr(builder, forCond);

        LLVMPositionBuilderAtEnd(builder, forCond);
        var iv = LLVMBuildLoad(builder, i, "iv");
        var cmp = LLVMBuildICmp(builder, LLVMIntSGE, iv, LLVMGetParam(fib, 0), "cmp");
        LLVMBuildCondBr(builder, cmp, forEnd, forBody);

        LLVMPositionBuilderAtEnd(builder, forBody);
        var av = LLVMBuildLoad(builder, a, "av");
        var bv = LLVMBuildLoad(builder, b, "bv");
        var c = LLVMBuildAdd(builder, av, bv, "c");
        LLVMBuildStore(builder, bv, a);
        LLVMBuildStore(builder, c, b);
        LLVMBuildBr(builder, forStep);

        LLVMPositionBuilderAtEnd(builder, forStep);
        iv = LLVMBuildLoad(builder, i, "iv");
        var inc = LLVMBuildAdd(builder, iv, LLVMConstInt(LLVMInt32Type(), 1, 0), "inc");
        LLVMBuildStore(builder, inc, i);
        LLVMBuildBr(builder, forCond);

        LLVMPositionBuilderAtEnd(builder, forEnd);
        av = LLVMBuildLoad(builder, a, "av");
        LLVMBuildRet(builder, av);

        var main = LLVMAddFunction(mod, "main",
                LLVMFunctionType(LLVMInt32Type(), new PointerPointer<>(), 0, 0));
        entry = LLVMAppendBasicBlock(main, "entry");
        LLVMPositionBuilderAtEnd(builder, entry);

        i = LLVMBuildCall(builder, fib,
                new PointerPointer<>(new LLVMValueRef[]{LLVMConstInt(LLVMInt32Type(), 10, 0)}),
                1, "i");
        LLVMBuildCall(builder, printd, new PointerPointer<>(new LLVMValueRef[]{i}), 1, "");
        LLVMBuildRet(builder, LLVMConstInt(LLVMInt32Type(), 0, 0));

        var outMessage = new BytePointer((Pointer) null);
        LLVMVerifyModule(mod, LLVMAbortProcessAction, outMessage);
        System.out.println(outMessage.getString());

        LLVMWriteBitcodeToFile(mod, "/Users/tigertang/CodeSandBox/fib.bc");

        LLVMDisposeMessage(outMessage);
        LLVMDisposeBuilder(builder);
        LLVMDisposeModule(mod);
    }

    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        LangLexer langLexer = new LangLexer(CharStreams.fromFileName(filePath));
        LangParser langParser = new LangParser(new CommonTokenStream(langLexer));

        ParseTree parseTree = langParser.program();
        ProgramListener programListener = new ProgramListener();
        ParseTreeWalker.DEFAULT.walk(programListener, parseTree);
        programListener.getGlobalScope().LogConstants();
        programListener.getGlobalScope().LogStructs();

        testLLVM();
    }
}

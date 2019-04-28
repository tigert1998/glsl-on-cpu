import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class Main {
    public static void error(String message) {
        System.err.println(message);
        System.exit(0);
    }

    private static void testLLVM() {
        var mod = LLVMModuleCreateWithName("fib");
        var builder = LLVMCreateBuilder();

        LLVMStructType(LLVMArrayType(LLVMFloatType(), 9), 1, 1);

        var printd = LLVMAddFunction(mod,"printd",
                LLVMFunctionType(LLVMVoidType(), LLVMInt32Type(), 1, 0));
        LLVMSetLinkage(printd, LLVMExternalLinkage);

        var fib = LLVMAddFunction(mod, "fib",
                LLVMFunctionType(LLVMInt32Type(), LLVMInt32Type(), 1, 0));

        var entry = LLVMAppendBasicBlock(fib, "entry");
        LLVMPositionBuilderAtEnd(builder, entry);
        var ap = LLVMBuildAlloca(builder, LLVMInt32Type(), "ap");
        var bp = LLVMBuildAlloca(builder, LLVMInt32Type(), "bp");
        LLVMBuildStore(builder, LLVMUtility.constant(1), ap);
        LLVMBuildStore(builder, LLVMUtility.constant(1), bp);

        var end = LLVMUtility.appendForLoop(fib, LLVMUtility.constant(1), LLVMGetParam(fib, 0), "for",
                (bodyBuilder -> {
                    var a = LLVMBuildLoad(bodyBuilder, ap, "a");
                    var b = LLVMBuildLoad(bodyBuilder, bp, "b");
                    var c = LLVMBuildAdd(bodyBuilder, a, b, "c");
                    LLVMBuildStore(bodyBuilder, b, ap);
                    LLVMBuildStore(bodyBuilder, c, bp);
                    return null;
        }));

        LLVMPositionBuilderAtEnd(builder, end);
        var a = LLVMBuildLoad(builder, ap, "a");
        LLVMBuildRet(builder, a);

        var main = LLVMAddFunction(mod, "main",
                LLVMFunctionType(LLVMInt32Type(), new PointerPointer<>(), 0, 0));
        entry = LLVMAppendBasicBlock(main, "entry");
        LLVMPositionBuilderAtEnd(builder, entry);

        var res = LLVMBuildCall(builder, fib,
                new PointerPointer<>(new LLVMValueRef[]{LLVMUtility.constant(10)}),
                1, "res");
        LLVMBuildCall(builder, printd, new PointerPointer<>(new LLVMValueRef[]{res}), 1, "");
        LLVMBuildRet(builder, LLVMUtility.constant(0));

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
        programListener.getScope().LogConstants();
        programListener.getScope().LogStructs();

        System.out.println("<<<<<<<");
        System.out.println(programListener.getProgramAST());
        System.out.println(">>>>>>>");

        for (var exception : programListener.getExceptionList()) {
            System.out.println(exception.getMessage());
        }

        testLLVM();
    }
}

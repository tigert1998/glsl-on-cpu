import org.bytedeco.llvm.LLVM.*;

import java.util.function.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class LLVMUtility {
    public static LLVMValueRef constant(int x) {
        return LLVMConstInt(LLVMInt32Type(), x, 1);
    }

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn,
                                                  LLVMValueRef l, LLVMValueRef r, String name,
                                                  Function<LLVMBuilderRef, Void> bodyAppender) {
        // [l, r)
        var builder = LLVMCreateBuilder();
        var before = LLVMGetLastBasicBlock(fn);

        var forCond = LLVMAppendBasicBlock(fn, name + ".cond");
        var forBody = LLVMAppendBasicBlock(fn, name + ".body");
        var forStep = LLVMAppendBasicBlock(fn, name + ".step");
        var forEnd = LLVMAppendBasicBlock(fn, name + ".end");

        LLVMPositionBuilderAtEnd(builder, before);
        var ip = LLVMBuildAlloca(builder, LLVMInt32Type(), name + ".ip");
        LLVMBuildStore(builder, l, ip);
        LLVMBuildBr(builder, forCond);

        LLVMPositionBuilderAtEnd(builder, forCond);
        var i = LLVMBuildLoad(builder, ip, name + ".i");
        var cmp = LLVMBuildICmp(builder, LLVMIntSGE, i, r, name + ".cmp");
        LLVMBuildCondBr(builder, cmp, forEnd, forBody);

        LLVMPositionBuilderAtEnd(builder, forBody);
        bodyAppender.apply(builder);
        LLVMBuildBr(builder, forStep);

        LLVMPositionBuilderAtEnd(builder, forStep);
        var lastI = LLVMBuildLoad(builder, ip, name + ".last.i");
        var nextI = LLVMBuildAdd(builder, lastI, constant(1), name + ".next.i");
        LLVMBuildStore(builder, nextI, ip);
        LLVMBuildBr(builder, forCond);

        return forEnd;
    }
}

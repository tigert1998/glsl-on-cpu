package codegen;

import org.bytedeco.llvm.LLVM.*;
import java.util.function.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class LLVMUtility {
    public static LLVMValueRef constant(int x) {
        return LLVMConstInt(LLVMInt32Type(), x, 1);
    }

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn,
                                                  LLVMValueRef l, LLVMValueRef r, String name,
                                                  BiFunction<LLVMBuilderRef, LLVMValueRef, Void> bodyAppender) {
        // [l, r)
        var builder = LLVMCreateBuilder();
        var firstBlock = LLVMGetFirstBasicBlock(fn);
        var lastBlock = LLVMGetLastBasicBlock(fn);

        var forCond = LLVMAppendBasicBlock(fn, name + ".for.cond");
        var forBody = LLVMAppendBasicBlock(fn, name + ".for.body");
        var forStep = LLVMAppendBasicBlock(fn, name + ".for.step");
        var forEnd = LLVMAppendBasicBlock(fn, name + ".for.end");

        LLVMPositionBuilderAtEnd(builder, firstBlock);
        var ip = LLVMBuildAlloca(builder, LLVMInt32Type(), name + ".for.ip");

        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildStore(builder, l, ip);
        LLVMBuildBr(builder, forCond);

        LLVMPositionBuilderAtEnd(builder, forCond);
        var i = LLVMBuildLoad(builder, ip, name + ".for.i");
        var cmp = LLVMBuildICmp(builder, LLVMIntSGE, i, r, name + ".for.cmp");
        LLVMBuildCondBr(builder, cmp, forEnd, forBody);

        LLVMPositionBuilderAtEnd(builder, forBody);
        bodyAppender.apply(builder, i);
        LLVMBuildBr(builder, forStep);

        LLVMPositionBuilderAtEnd(builder, forStep);
        var lastI = LLVMBuildLoad(builder, ip, name + ".for.last.i");
        var nextI = LLVMBuildAdd(builder, lastI, constant(1), name + ".for.next.i");
        LLVMBuildStore(builder, nextI, ip);
        LLVMBuildBr(builder, forCond);

        return forEnd;
    }
}

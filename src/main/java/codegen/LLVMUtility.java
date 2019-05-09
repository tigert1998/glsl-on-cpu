package codegen;

import org.bytedeco.javacpp.PointerPointer;
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
        var lastBlock = LLVMGetLastBasicBlock(fn);

        var forCond = LLVMAppendBasicBlock(fn, name + ".for.cond");
        var forBody = LLVMAppendBasicBlock(fn, name + ".for.body");
        var forStep = LLVMAppendBasicBlock(fn, name + ".for.step");
        var forEnd = LLVMAppendBasicBlock(fn, name + ".for.end");

        var ip = buildAllocaInFirstBlock(fn, LLVMInt32Type(), name + ".for.ip");

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

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn, int[] indices,
                                                  BiFunction<LLVMBuilderRef, Integer, Void> bodyAppender) {
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(fn));
        for (int i = 0; i < indices.length; i++) {
            bodyAppender.apply(builder, i);
        }
        return LLVMGetLastBasicBlock(fn);
    }

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn, int l, int r, String name,
                                                  BiFunction<LLVMBuilderRef, LLVMValueRef, Void> bodyAppender) {
        return appendForLoop(fn, constant(l), constant(r), name, bodyAppender);
    }

    public static LLVMValueRef buildGEP(LLVMBuilderRef builder, LLVMValueRef pointer, LLVMValueRef[] indices, String name) {
        return LLVMBuildGEP(builder, pointer, new PointerPointer<>(indices), indices.length, name);
    }

    public static LLVMValueRef buildGEP(LLVMBuilderRef builder, LLVMValueRef pointer, String name, LLVMValueRef... indices) {
        return buildGEP(builder, pointer, indices, name);
    }

    public static LLVMValueRef buildGEP(LLVMBuilderRef builder, LLVMValueRef pointer, int[] indices, String name) {
        var newIndices = new LLVMValueRef[indices.length];
        for (int i = 0; i < indices.length; i++) newIndices[i] = constant(indices[i]);
        return buildGEP(builder, pointer, newIndices, name);
    }

    public static LLVMValueRef buildAllocaInFirstBlock(LLVMValueRef function, LLVMTypeRef type, String name) {
        var block = LLVMGetFirstBasicBlock(function);
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, block);
        return LLVMBuildAlloca(builder, type, name);
    }

    public static void log(LLVMValueRef value) {
        System.out.println(LLVMPrintValueToString(value).getString());
    }
}

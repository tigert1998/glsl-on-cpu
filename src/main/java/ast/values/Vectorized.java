package ast.values;

import ast.types.Type;
import ast.types.VectorizedType;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public interface Vectorized {
    Value[] retrieve();

    static LLVMValueRef inLLVM(Vectorized value) {
        var values = value.retrieve();
        var llvmValues = new LLVMValueRef[values.length];
        for (int i = 0; i < values.length; i++) llvmValues[i] = values[i].inLLVM();
        return LLVMConstArray(((VectorizedType) (((Value) value).type)).primitiveType().inLLVM(),
                new PointerPointer<>(llvmValues), llvmValues.length);
    }

    // [n x i32*]*
    static LLVMValueRef ptrInLLVM(Vectorized value, LLVMValueRef function) {
        var values = value.retrieve();
        var type = (VectorizedType) ((Value) value).getType();
        var ptrArrType = LLVMArrayType(LLVMPointerType(type.primitiveType().inLLVM(), 0), values.length);

        var firstBlock = LLVMGetFirstBasicBlock(function);
        var lastBlock = LLVMGetLastBasicBlock(function);
        var builder = LLVMCreateBuilder();

        LLVMPositionBuilderAtEnd(builder, firstBlock);
        var arrPtr = LLVMBuildAlloca(builder, ((Type) type).inLLVM(), "");
        var ptrArrPtr = LLVMBuildAlloca(builder, ptrArrType, "");

        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildStore(builder, inLLVM(value), arrPtr);

        appendForLoop(function, constant(0), constant(values.length), "", (bodyBuilder, i) -> {
            var indices = new LLVMValueRef[]{constant(0), i};
            var ptrPtr = LLVMBuildGEP(bodyBuilder, ptrArrPtr, new PointerPointer<>(indices), indices.length, "");
            var ptr = LLVMBuildGEP(bodyBuilder, arrPtr, new PointerPointer<>(indices), indices.length, "");
            LLVMBuildStore(bodyBuilder, ptr, ptrPtr);
            return null;
        });

        return ptrArrPtr;
    }
}

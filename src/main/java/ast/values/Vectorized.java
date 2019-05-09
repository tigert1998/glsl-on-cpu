package ast.values;

import ast.types.*;
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

    // [n x elementType*]*
    static LLVMValueRef ptrInLLVM(Vectorized value, LLVMValueRef function) {
        var type = ((Value) value).getType();
        var vectorizedType = (VectorizedType) type;

        var arrPtr = buildAllocaInFirstBlock(function, type.inLLVM(), "");
        var ptrArrPtr = buildAllocaInFirstBlock(function, type.withInnerPtrInLLVM(), "");

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        LLVMBuildStore(builder, inLLVM(value), arrPtr);

        appendForLoop(function, constant(0), constant(vectorizedType.vectorizedLength()), "ptr_arr_ptr",
                (bodyBuilder, i) -> {
                    var indices = new LLVMValueRef[]{constant(0), i};
                    var ptrPtr = buildGEP(bodyBuilder, ptrArrPtr, indices, "");
                    var ptr = buildGEP(bodyBuilder, arrPtr, indices, "");
                    LLVMBuildStore(bodyBuilder, ptr, ptrPtr);
                    return null;
                });

        return ptrArrPtr;
    }
}

package ast.values;

import ast.types.VectorizedType;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import static org.bytedeco.llvm.global.LLVM.LLVMConstArray;

public interface Vectorized {
    Value[] retrieve();

    static LLVMValueRef inLLVM(Vectorized value) {
        var values = value.retrieve();
        var llvmValues = new LLVMValueRef[values.length];
        for (int i = 0; i < values.length; i++) llvmValues[i] = values[i].inLLVM();
        return LLVMConstArray(((VectorizedType) (((Value) value).type)).primitiveType().inLLVM(),
                new PointerPointer<>(llvmValues), llvmValues.length);
    }
}

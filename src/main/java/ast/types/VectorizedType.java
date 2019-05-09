package ast.types;

import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;


public interface VectorizedType {
    Type primitiveType();

    int vectorizedLength();

    static LLVMTypeRef withInnerPtrInLLVM(VectorizedType type) {
        return LLVMArrayType(LLVMPointerType(type.primitiveType().inLLVM(), 0), type.vectorizedLength());
    }
}

package ast.types;

import ast.*;
import org.bytedeco.llvm.LLVM.*;

import static codegen.LLVMUtility.*;
import static org.bytedeco.llvm.global.LLVM.*;


public interface VectorizedType {
    Type primitiveType();

    int vectorizedLength();

    static LLVMTypeRef withInnerPtrInLLVM(VectorizedType type) {
        return LLVMArrayType(LLVMPointerType(type.primitiveType().inLLVM(), 0), type.vectorizedLength());
    }

    static LLVMValueRef construct(VectorizedType targetType, Type[] types, LLVMValueRef[] values,
                                  LLVMValueRef function, Scope scope) {
        var builder = LLVMCreateBuilder();
        var result = buildAllocaInFirstBlock(function, ((Type) targetType).inLLVM(), "");

        if (types.length == 1 && !(types[0] instanceof VectorizedType)) {
            LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));

            var from = LLVMBuildLoad(builder, targetType.primitiveType().construct(types, values, function, scope), "");
            appendForLoop(function, 0, targetType.vectorizedLength(), "construct_vectorized", (bodyBuilder, i) -> {
                var to = buildGEP(bodyBuilder, result, "", constant(0), i);
                LLVMBuildStore(bodyBuilder, from, to);
                return null;
            });

            return loadPtr((Type) targetType, function, result);
        }

        int current = 0, total = targetType.vectorizedLength();
        for (int i = 0; i < types.length; i++) {
            if (types[i] instanceof VectorizedType) {
                var type = (VectorizedType) types[i];
                var value = values[i];
                int delta = Math.min(type.vectorizedLength(), total - current);
                var currentLLVMValue = constant(current);

                appendForLoop(function, 0, delta, targetType + "_from_" + type, (bodyBuilder, index) -> {
                    var tmp = buildLoad(bodyBuilder, buildGEP(bodyBuilder, value, "", constant(0), index));
                    var from = buildLoad(bodyBuilder,
                            targetType.primitiveType().construct(type.primitiveType(), tmp, function, scope));
                    var to = buildGEP(bodyBuilder, result, "", constant(0),
                            LLVMBuildAdd(bodyBuilder, index, currentLLVMValue, ""));
                    LLVMBuildStore(bodyBuilder, from, to);
                    return null;
                });
                current += delta;
            } else {
                LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
                var from = buildLoad(builder, targetType.primitiveType().construct(types[i], values[i], function, scope));
                var to = buildGEP(builder, result, "", 0, current);
                LLVMBuildStore(builder, from, to);
                current += 1;
            }
            if (current >= total)
                return loadPtr((Type) targetType, function, result);
        }
        return null;
    }
}

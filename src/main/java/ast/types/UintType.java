package ast.types;

import ast.*;
import ast.exceptions.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;

import static codegen.LLVMUtility.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class UintType extends Type implements IncreasableType {
    public static UintType TYPE = new UintType();

    private static UintValue zero = new UintValue(0);
    private static UintValue one = new UintValue(1);

    @Override
    public boolean equals(Object obj) {
        return UintType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "uint";
    }

    @Override
    public UintValue zero() {
        return zero;
    }

    @Override
    public UintValue one() {
        return one;
    }

    @Override
    public UintValue construct(Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return (UintValue) value;
        } else if (value instanceof BoolValue) {
            return new UintValue(((BoolValue) value).value ? 1 : 0);
        } else if (value instanceof FloatValue) {
            return new UintValue((int) (((FloatValue) value).value));
        } else if (value instanceof IntValue) {
            return new UintValue(((IntValue) value).value);
        } else if (value instanceof Vectorized) {
            return UintType.TYPE.construct(new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), UintType.TYPE);
    }

    @Override
    public LLVMTypeRef inLLVM() {
        return LLVMInt32Type();
    }

    @Override
    public LLVMValueRef construct(Type[] types, LLVMValueRef[] values, LLVMValueRef function, Scope scope) {
        var type = types[0];
        var valuePtr = values[0];
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));

        if (type instanceof IntType || type instanceof UintType) {
            return valuePtr;
        } else if (type instanceof BoolType) {
            var from = LLVMBuildIntCast2(builder,
                    LLVMBuildLoad(builder, valuePtr, ""), LLVMInt32Type(), 0, "");
            var to = buildAllocaInFirstBlock(function, LLVMInt32Type(), "");
            LLVMBuildStore(builder, from, to);
            return to;
        } else if (type instanceof FloatType) {
            var from = LLVMBuildFPToUI(builder, LLVMBuildLoad(builder, valuePtr, ""), LLVMInt32Type(), "");
            var to = buildAllocaInFirstBlock(function, LLVMInt32Type(), "");
            LLVMBuildStore(builder, from, to);
            return to;
        } else {
            var from = LLVMBuildLoad(builder, buildGEP(builder, valuePtr, "", 0, 0), "");
            return construct(((VectorizedType) type).primitiveType(), from, function, scope);
        }
    }
}

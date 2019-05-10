package ast.types;

import ast.*;
import ast.exceptions.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;

import static codegen.LLVMUtility.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class BoolType extends Type {
    public static BoolType TYPE = new BoolType();

    private static BoolValue zero = new BoolValue(false);

    @Override
    public boolean equals(Object obj) {
        return BoolType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "bool";
    }

    @Override
    public BoolValue zero() {
        return zero;
    }

    @Override
    public BoolValue construct(Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return new BoolValue(((UintValue) value).value != 0);
        } else if (value instanceof BoolValue) {
            return (BoolValue) value;
        } else if (value instanceof FloatValue) {
            return new BoolValue(((FloatValue) value).value != 0.f);
        } else if (value instanceof IntValue) {
            return new BoolValue(((IntValue) value).value != 0);
        } else if (value instanceof Vectorized) {
            return BoolType.TYPE.construct(new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), BoolType.TYPE);
    }

    @Override
    public LLVMTypeRef inLLVM() {
        return LLVMInt8Type();
    }

    @Override
    public LLVMValueRef construct(Type[] types, LLVMValueRef[] values, LLVMValueRef function, Scope scope) {
        var type = types[0];
        var valuePtr = values[0];
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));

        if (type instanceof IntType || type instanceof UintType) {
            var from = LLVMBuildIntCast2(builder,
                    LLVMBuildICmp(builder, LLVMIntNE,
                            LLVMBuildLoad(builder, valuePtr, ""),
                            constant(0), ""),
                    LLVMInt8Type(), 0, "");
            var to = buildAllocaInFirstBlock(function, LLVMInt8Type(), "");
            LLVMBuildStore(builder, from, to);
            return to;
        } else if (type instanceof BoolType) {
            return valuePtr;
        } else if (type instanceof FloatType) {
            var from = LLVMBuildIntCast2(builder,
                    LLVMBuildFCmp(builder, LLVMRealONE,
                            LLVMBuildLoad(builder, valuePtr, ""),
                            constant(0.f), ""),
                    LLVMInt8Type(), 0, "");
            var to = buildAllocaInFirstBlock(function, LLVMInt8Type(), "");
            LLVMBuildStore(builder, from, to);
            return to;
        } else {
            var from = LLVMBuildLoad(builder, buildGEP(builder, valuePtr, "", 0, 0), "");
            return construct(((VectorizedType) type).primitiveType(), from, function, scope);
        }
    }
}

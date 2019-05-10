package ast.types;

import ast.*;
import ast.exceptions.*;
import ast.expr.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class IntType extends Type implements IncreasableType {
    public static IntType TYPE = new IntType();

    private static IntValue zero = new IntValue(0);
    private static IntValue one = new IntValue(1);

    @Override
    public boolean equals(Object obj) {
        return IntType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "int";
    }

    @Override
    public IntValue zero() {
        return zero;
    }

    @Override
    public IntValue one() {
        return one;
    }

    @Override
    public IntValue construct(Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return new IntValue((int) (long) ((UintValue) value).value);
        } else if (value instanceof BoolValue) {
            return new IntValue(((BoolValue) value).value ? 1 : 0);
        } else if (value instanceof FloatValue) {
            return new IntValue((int) (((FloatValue) value).value));
        } else if (value instanceof IntValue) {
            return (IntValue) value;
        } else if (value instanceof Vectorized) {
            return IntType.TYPE.construct(new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), IntType.TYPE);
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
            var from = LLVMBuildFPToSI(builder, LLVMBuildLoad(builder, valuePtr, ""), LLVMInt32Type(), "");
            var to = buildAllocaInFirstBlock(function, LLVMInt32Type(), "");
            LLVMBuildStore(builder, from, to);
            return to;
        } else {
            var from = LLVMBuildLoad(builder, buildGEP(builder, valuePtr, "", 0, 0), "");
            return construct(((VectorizedType) type).primitiveType(), from, function, scope);
        }
    }
}

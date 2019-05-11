package ast.types;

import ast.*;
import ast.exceptions.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class ArrayType extends Type implements IndexedType {
    private Type type;
    private int n;
    private boolean unknownN;

    @Override
    public Type elementType() {
        return type;
    }

    public ArrayType(Type type, int n) {
        this.type = type;
        this.n = n;
        unknownN = false;
    }

    public ArrayType(Type type) {
        this.type = type;
        unknownN = true;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int getN() {
        return n;
    }

    public void setN(int length) {
        unknownN = false;
        this.n = length;
    }

    public boolean isUnknownN() {
        return unknownN;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayType)) return false;
        ArrayType arrObj = (ArrayType) obj;
        if (!arrObj.type.equals(this.type)) return false;
        if (arrObj.unknownN || this.unknownN) return true;
        return arrObj.n == this.n;
    }

    @Override
    public String toString() {
        if (unknownN) return type + "[]";
        return type + "[" + n + "]";
    }

    @Override
    public ArrayValue zero() {
        return new ArrayValue(this, type.zero());
    }

    @Override
    public ArrayValue construct(Value[] values) throws ConstructionFailedException {
        if (!isUnknownN() && getN() != values.length)
            throw ConstructionFailedException.arraySizeUnmatched();
        if (values.length == 0)
            throw ConstructionFailedException.arraySizeNotPositive();
        for (var value : values) {
            if (!value.getType().equals(getType()))
                throw ConstructionFailedException.arrayIncorrectType();
        }
        setN(values.length);
        return new ArrayValue(this, values);
    }

    @Override
    public LLVMTypeRef inLLVM() {
        return LLVMArrayType(elementType().inLLVM(), getN());
    }

    @Override
    public LLVMValueRef construct(Type[] types, LLVMValueRef[] values, LLVMValueRef function, Scope scope) {
        var result = buildAllocaInFirstBlock(function, this.inLLVM(), "");
        if (type instanceof VectorizedType) {
            var builder = LLVMCreateBuilder();
            LLVMValueRef tmp = buildAllocaInFirstBlock(function, type.inLLVM(), "");
            for (int i = 0; i < values.length; i++) {
                var value = values[i];
                appendForLoop(function, 0, ((VectorizedType) type).vectorizedLength(), "",
                        (bodyBuilder, index) -> {
                            var to = buildGEP(bodyBuilder, tmp, "", constant(0), index);
                            var from = buildLoad(bodyBuilder, buildLoad(bodyBuilder,
                                    buildGEP(bodyBuilder, value, "", constant(0), index)));
                            LLVMBuildStore(bodyBuilder, from, to);
                            return null;
                        });
                LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
                LLVMBuildStore(builder, buildLoad(builder, tmp), buildGEP(builder, result, "", 0, i));
            }
        } else {
            var builder = LLVMCreateBuilder();
            LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
            for (int i = 0; i < values.length; i++) {
                var to = buildGEP(builder, result, "", 0, i);
                var from = buildLoad(builder, values[i]);
                LLVMBuildStore(builder, from, to);
            }
        }
        return result;
    }
}

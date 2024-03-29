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
        var builder = LLVMCreateBuilder();
        for (int i = 0; i < values.length; i++) {
            LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
            storePtr(getType(), function, values[i], buildGEP(builder, result, "", 0, i));
        }
        return result;
    }
}

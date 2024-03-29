package ast.types;

import ast.*;
import ast.exceptions.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class BvecnType extends Type implements SwizzledType {
    private int n;

    // prevent multiple new
    private static BvecnType[] predefinedTypes = new BvecnType[3];
    private static BvecnValue[] zeros = new BvecnValue[3];

    static {
        for (int i = 2; i <= 4; i++) {
            predefinedTypes[i - 2] = new BvecnType(i);
            zeros[i - 2] = new BvecnValue(predefinedTypes[i - 2], BoolType.TYPE.zero());
        }
    }

    @Override
    public Type elementType() {
        return BoolType.TYPE;
    }

    @Override
    public Type primitiveType() {
        return BoolType.TYPE;
    }

    static public BvecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public int vectorizedLength() {
        return n;
    }

    @Override
    public SwizzledType changeN(int n) {
        return fromN(n);
    }

    static public BvecnType fromText(String text) {
        int digit = text.charAt(text.length() - 1) - '0';
        return fromN(digit);
    }

    private BvecnType(int n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        if (BvecnType.class != obj.getClass()) return false;
        return ((BvecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "bvec" + n;
    }

    @Override
    public BvecnValue zero() {
        return zeros[n - 2];
    }

    @Override
    public BvecnValue construct(Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new BvecnValue(this, BoolType.TYPE.construct(values));
        }
        List<BoolValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(BoolType.TYPE.construct(new Value[]{newValue}));
            } else {
                valueList.add(BoolType.TYPE.construct(new Value[]{value}));
            }
        }
        if (valueList.size() < this.getN())
            throw ConstructionFailedException.notEnoughData();
        return new BvecnValue(this, valueList);
    }

    @Override
    public LLVMTypeRef inLLVM() {
        return LLVMArrayType(elementType().inLLVM(), getN());
    }

    @Override
    public LLVMTypeRef withInnerPtrInLLVM() {
        return VectorizedType.withInnerPtrInLLVM(this);
    }

    @Override
    public LLVMValueRef construct(Type[] types, LLVMValueRef[] values, LLVMValueRef function, Scope scope) {
        return VectorizedType.construct(this, types, values, function, scope);
    }
}

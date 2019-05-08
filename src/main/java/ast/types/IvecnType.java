package ast.types;

import ast.exceptions.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;
import java.util.*;

public class IvecnType extends Type implements SwizzledType, IncreasableType {
    private int n;

    // prevent multiple new
    private static IvecnType[] predefinedTypes = new IvecnType[3];
    private static IvecnValue[] zeros = new IvecnValue[3];
    private static IvecnValue[] ones = new IvecnValue[3];

    static {
        for (int i = 2; i <= 4; i++) {
            predefinedTypes[i - 2] = new IvecnType(i);
            zeros[i - 2] = new IvecnValue(predefinedTypes[i - 2], IntType.TYPE.zero());
            ones[i - 2] = new IvecnValue(predefinedTypes[i - 2], IntType.TYPE.one());
        }
    }

    @Override
    public Type elementType() {
        return IntType.TYPE;
    }

    @Override
    public Type primitiveType() {
        return IntType.TYPE;
    }

    private IvecnType(int n) {
        this.n = n;
    }

    static public IvecnType fromN(int n) {
        return predefinedTypes[n - 2];
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public SwizzledType changeN(int n) {
        return fromN(n);
    }

    static public IvecnType fromText(String text) {
        int digit = text.charAt(text.length() - 1) - '0';
        return fromN(digit);
    }

    @Override
    public boolean equals(Object obj) {
        if (IvecnType.class != obj.getClass()) return false;
        return ((IvecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "ivec" + n;
    }

    @Override
    public IvecnValue zero() {
        return zeros[n - 2];
    }

    @Override
    public Value one() {
        return ones[n - 2];
    }

    @Override
    public IvecnValue construct(Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new IvecnValue(this, IntType.TYPE.construct(values));
        }
        List<IntValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(IntType.TYPE.construct(new Value[]{newValue}));
            } else {
                valueList.add(IntType.TYPE.construct(new Value[]{value}));
            }
        }
        if (valueList.size() < this.getN())
            throw ConstructionFailedException.notEnoughData();
        return new IvecnValue(this, valueList);
    }

    @Override
    public LLVMTypeRef inLLVM() {
        return LLVMArrayType(elementType().inLLVM(), getN());
    }
}

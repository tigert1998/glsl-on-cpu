package ast.types;

import ast.exceptions.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
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
            return new UintValue(dropFractionPart(((FloatValue) value).value));
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
}

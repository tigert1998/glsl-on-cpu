package ast.types;

import ast.exceptions.ConstructionFailedException;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

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
            return new IntValue(dropFractionPart(((FloatValue) value).value));
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
}

package ast.types;

import ast.exceptions.ConstructionFailedException;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
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
}

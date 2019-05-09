package ast.types;

import ast.exceptions.*;
import ast.expr.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;

import java.util.*;

public abstract class Type {
    abstract public Value zero();

    abstract public Value construct(Value[] values) throws ConstructionFailedException;

    // for syntax checking
    public void construct(Expr[] exprs) throws ConstructionFailedException {
        var defaultValues = new Value[exprs.length];
        for (int i = 0; i < exprs.length; i++) defaultValues[i] = exprs[i].getType().zero();
        construct(defaultValues);
    }

    static Value extractSoleParameter(Value[] values) throws ConstructionFailedException {
        if (values.length == 0)
            throw ConstructionFailedException.noArgument();
        if (values.length >= 2)
            throw ConstructionFailedException.tooManyArguments();
        return values[0];
    }

    static int dropFractionPart(float v) {
        return (int) (v - (v % 1));
    }

    static List<FloatValue> flattenThenConvertToFloatValue(Value[] values)
            throws ConstructionFailedException {
        List<FloatValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(FloatType.TYPE.construct(new Value[]{newValue}));
            } else {
                valueList.add(FloatType.TYPE.construct(new Value[]{value}));
            }
        }
        return valueList;
    }

    // i32, float, i8
    // [n x i32] [n x float] [n x i8]
    public abstract LLVMTypeRef inLLVM();

    // i32, float, i8
    // [n x i32*] [n x float*] [n x i8*]
    public LLVMTypeRef withInnerPtrInLLVM() {
        return inLLVM();
    }
}

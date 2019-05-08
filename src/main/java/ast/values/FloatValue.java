package ast.values;

import ast.types.*;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class FloatValue extends Value {
    public float value;

    public FloatValue(float value) {
        this.value = value;
        this.type = FloatType.TYPE;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloatValue)) return false;
        var bool = (FloatValue) obj;
        return bool.value == this.value;
    }

    @Override
    public LLVMValueRef inLLVM() {
        return LLVMConstReal(type.inLLVM(), value);
    }
}

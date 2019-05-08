package ast.values;

import ast.types.*;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class IntValue extends Value {
    public int value;

    public IntValue(int value) {
        this.value = value;
        this.type = IntType.TYPE;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntValue)) return false;
        var bool = (IntValue) obj;
        return bool.value == this.value;
    }

    @Override
    public LLVMValueRef inLLVM() {
        return LLVMConstInt(type.inLLVM(), value, 1);
    }
}

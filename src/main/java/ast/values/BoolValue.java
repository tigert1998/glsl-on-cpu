package ast.values;

import ast.types.*;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class BoolValue extends Value {
    public boolean value;
    
    public BoolValue(boolean value) {
        this.value = value;
        this.type = BoolType.TYPE;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoolValue)) return false;
        var bool = (BoolValue) obj;
        return bool.value == this.value;
    }

    @Override
    public LLVMValueRef inLLVM() {
        return LLVMConstInt(type.inLLVM(), value ? 1 : 0, 0);
    }
}

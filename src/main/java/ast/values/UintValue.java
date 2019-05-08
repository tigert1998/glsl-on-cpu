package ast.values;

import ast.types.*;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class UintValue extends Value {
    public long value;

    public UintValue(long value) {
        this.value = value;
        this.type = UintType.TYPE;
    }

    @Override
    public String toString() {
        return value + "u";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UintValue)) return false;
        var uint = (UintValue) obj;
        return uint.value == this.value;
    }

    @Override
    public LLVMValueRef inLLVM() {
        return LLVMConstInt(type.inLLVM(), value, 0);
    }
}

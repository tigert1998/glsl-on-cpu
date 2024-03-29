package ast.values;

import ast.exceptions.*;
import ast.types.*;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import static org.bytedeco.llvm.global.LLVM.*;

public class StructValue extends Value implements Selected {
    public Value[] values;

    public StructValue(StructType type, Value[] values) {
        this.values = values;
        this.type = type;
    }

    @Override
    public Value select(String name) throws InvalidSelectionException {
        Integer idx = ((StructType) type).getFieldInfoIndex(name);
        if (idx == null) throw InvalidSelectionException.noSuchField(name);
        return values[idx];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < values.length; i++) {
            var field = ((StructType) type).getFieldInfo(i);
            sb.append('.').append(field.id).append(" = ");
            sb.append(values[i]);
            if (i < values.length - 1) sb.append(", ");
        }
        sb.append("}");
        return new String(sb);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StructValue)) return false;
        var struct = (StructValue) obj;
        if (!struct.getType().equals(this.getType())) return false;
        for (int i = 0; i < values.length; i++) {
            if (!struct.values[i].equals(values[i])) return false;
        }
        return true;
    }

    @Override
    public LLVMValueRef inLLVM() {
        var llvmValues = new LLVMValueRef[this.values.length];
        for (int i = 0; i < this.values.length; i++) llvmValues[i] = values[i].inLLVM();
        return LLVMConstStruct(new PointerPointer<>(llvmValues), llvmValues.length, 0);
    }
}

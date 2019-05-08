package ast.types;

import ast.values.Value;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class VoidType extends Type {
    public static Type TYPE = new VoidType();

    @Override
    public Value zero() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public Value construct(Value[] values) {
        return null;
    }

    @Override
    public LLVMTypeRef inLLVM() {
        return LLVMVoidType();
    }
}

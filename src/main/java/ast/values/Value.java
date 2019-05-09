package ast.values;

import ast.exceptions.*;
import ast.types.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public abstract class Value {
    protected Type type = null;

    public Type getType() {
        return type;
    }

    static public int evalAsIntegral(Value value) throws UnlocatedSyntaxErrorException {
        if (!(value.getType() instanceof IntType || value.getType() instanceof UintType))
            throw UnlocatedSyntaxErrorException.notIntegerExpression();
        int res;
        if (value instanceof IntValue) res = ((IntValue) value).value;
        else res = (int) (long) ((UintValue) value).value;
        return res;
    }

    abstract public LLVMValueRef inLLVM();

    public LLVMValueRef ptrInLLVM(LLVMValueRef function) {
        var ptr = buildAllocaInFirstBlock(function, type.inLLVM(), "");

        var lastBlock = LLVMGetLastBasicBlock(function);
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildStore(builder, inLLVM(), ptr);
        return ptr;
    }
}

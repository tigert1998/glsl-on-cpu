package ast.operators;

import ast.Scope;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import java.lang.reflect.InvocationTargetException;

import static codegen.LLVMUtility.*;
import static org.bytedeco.llvm.global.LLVM.*;

public interface UnaryOperator {
    default Value apply(Value x) throws OperatorCannotBeAppliedException {
        // check syntax
        apply(x.getType());
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass());
            return (Value) method.invoke(this, x);
        } catch (InvocationTargetException exception) {
            throw (ArithmeticException) exception.getCause();
        } catch (Exception ignore) {
            return null;
        }
    }

    default Type apply(Type x) throws OperatorCannotBeAppliedException {
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass());
            return (Type) method.invoke(this, x);
        } catch (Exception exception) {
            throw new OperatorCannotBeAppliedException((Operator) this, x);
        }
    }

    default LLVMValueRef apply(Type type, LLVMValueRef value,
                               LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var id = "." + ((Operator) this).getLLVMID() + "." + type.getLLVMID();
        Type returnType;
        try {
            returnType = apply(type);
        } catch (OperatorCannotBeAppliedException ignore) {
            return null;
        }
        var toBeCalled = scope.builtInFunctions.computeIfAbsent(id, (k) -> {
            var funcType = builtInFuncType(returnType, new Type[]{type});
            var func = LLVMAddFunction(module, id, funcType);
            LLVMSetLinkage(func, LLVMExternalLinkage);
            return func;
        });
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        var result = buildAllocaInFirstBlock(function, returnType.inLLVM(), "");
        var parameterArr = constructBuiltInFuncParameters(
                new Type[]{type}, new LLVMValueRef[]{value},
                returnType, result, function);
        LLVMBuildCall(builder, toBeCalled,
                new PointerPointer<>(parameterArr), parameterArr.length, "");
        return loadPtr(returnType, function, result);
    }
}

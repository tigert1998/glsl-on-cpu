package ast.operators;

import ast.*;
import ast.exceptions.*;
import ast.values.*;
import ast.types.*;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static codegen.LLVMUtility.*;
import static org.bytedeco.llvm.global.LLVM.*;

public interface BinaryOperator {
    default Value apply(Value x, Value y) throws OperatorCannotBeAppliedException, ArithmeticException {
        // check syntax
        apply(x.getType(), y.getType());
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass(), y.getClass());
            return (Value) method.invoke(this, x, y);
        } catch (InvocationTargetException exception) {
            throw (ArithmeticException) exception.getCause();
        } catch (Exception ignore) {
            return null;
        }
    }

    default Type apply(Type x, Type y) throws OperatorCannotBeAppliedException {
        try {
            var method = this.getClass().getDeclaredMethod("apply", x.getClass(), y.getClass());
            return (Type) method.invoke(this, x, y);
        } catch (Exception exception) {
            throw new OperatorCannotBeAppliedException((Operator) this, x, y);
        }
    }

    default LLVMValueRef apply(Type xtype, Type ytype, LLVMValueRef xvalue, LLVMValueRef yvalue,
                               LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var id = "." + ((Operator) this).getLLVMID() + "." + xtype.getLLVMID() + "." + ytype.getLLVMID();
        Type returnType;
        try {
            returnType = apply(xtype, ytype);
        } catch (OperatorCannotBeAppliedException ignore) {
            return null;
        }
        var toBeCalled = scope.builtInFunctions.computeIfAbsent(id, (k) -> {
            var funcType = builtInFuncType(returnType, new Type[]{xtype, ytype});
            var func = LLVMAddFunction(module, id, funcType);
            LLVMSetLinkage(func, LLVMExternalLinkage);
            return func;
        });
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        var result = buildAllocaInFirstBlock(function, returnType.inLLVM(), "");
        var parameterArr = constructBuiltInFuncParameters(
                new Type[]{xtype, ytype}, new LLVMValueRef[]{xvalue, yvalue},
                returnType, result, function);

        LLVMBuildCall(builder, toBeCalled,
                new PointerPointer<>(parameterArr), parameterArr.length, "");
        return loadPtr(returnType, function, result);
    }
}

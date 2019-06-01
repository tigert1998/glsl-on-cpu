package codegen;

import ast.types.*;
import ast.values.*;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import util.*;

import java.util.*;
import java.util.function.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class LLVMUtility {
    public static LLVMValueRef constant(long x) {
        return LLVMConstInt(LLVMInt32Type(), x, 0);
    }

    public static LLVMValueRef constant(int x) {
        return LLVMConstInt(LLVMInt32Type(), x, 1);
    }

    public static LLVMValueRef constant(float x) {
        return LLVMConstReal(LLVMFloatType(), x);
    }

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn, LLVMValueRef l, LLVMValueRef r,
                                                  LLVMValueRef ip, String name,
                                                  BiFunction<LLVMBuilderRef, LLVMValueRef, Void> bodyAppender) {
        // last block
        var builder = LLVMCreateBuilder();
        var lastBlock = LLVMGetLastBasicBlock(fn);
        var forCond = LLVMAppendBasicBlock(fn, name + ".for.cond");
        LLVMPositionBuilderAtEnd(builder, lastBlock);
        LLVMBuildStore(builder, l, ip);
        LLVMBuildBr(builder, forCond);

        // forCond
        LLVMPositionBuilderAtEnd(builder, forCond);
        var i = LLVMBuildLoad(builder, ip, name + ".for.i");
        var cmp = LLVMBuildICmp(builder, LLVMIntSGE, i, r, name + ".for.cmp");

        // forBody
        var forBody = LLVMAppendBasicBlock(fn, name + ".for.body");
        LLVMPositionBuilderAtEnd(builder, forBody);
        bodyAppender.apply(builder, i);
        var forStep = LLVMAppendBasicBlock(fn, name + ".for.step");
        var forEnd = LLVMAppendBasicBlock(fn, name + ".for.end");
        LLVMBuildBr(builder, forStep);

        // forCond
        LLVMPositionBuilderAtEnd(builder, forCond);
        LLVMBuildCondBr(builder, cmp, forEnd, forBody);

        // forStep
        LLVMPositionBuilderAtEnd(builder, forStep);
        var lastI = LLVMBuildLoad(builder, ip, name + ".for.last.i");
        var nextI = LLVMBuildAdd(builder, lastI, constant(1), name + ".for.next.i");
        LLVMBuildStore(builder, nextI, ip);
        LLVMBuildBr(builder, forCond);

        LLVMDisposeBuilder(builder);
        return forEnd;
    }

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn, LLVMValueRef l, LLVMValueRef r, String name,
                                                  BiFunction<LLVMBuilderRef, LLVMValueRef, Void> bodyAppender) {
        // [l, r)
        var ip = buildAllocaInFirstBlock(fn, LLVMInt32Type(), name + ".for.ip");
        return appendForLoop(fn, l, r, ip, name, bodyAppender);
    }

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn, int[] indices,
                                                  TriFunction<LLVMBuilderRef, Integer, LLVMValueRef, Void> bodyAppender) {
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(fn));
        for (int i = 0; i < indices.length; i++) {
            bodyAppender.apply(builder, i, constant(indices[i]));
        }
        LLVMDisposeBuilder(builder);
        return LLVMGetLastBasicBlock(fn);
    }

    public static LLVMBasicBlockRef appendForLoop(LLVMValueRef fn, int l, int r, String name,
                                                  BiFunction<LLVMBuilderRef, LLVMValueRef, Void> bodyAppender) {
        return appendForLoop(fn, constant(l), constant(r), name, bodyAppender);
    }

    public static LLVMValueRef buildGEP(LLVMBuilderRef builder, LLVMValueRef pointer, LLVMValueRef[] indices, String name) {
        return LLVMBuildGEP(builder, pointer, new PointerPointer<>(indices), indices.length, name);
    }

    public static LLVMValueRef buildGEP(LLVMBuilderRef builder, LLVMValueRef pointer, String name, LLVMValueRef... indices) {
        return buildGEP(builder, pointer, indices, name);
    }

    public static LLVMValueRef buildGEP(LLVMBuilderRef builder, LLVMValueRef pointer, int[] indices, String name) {
        var newIndices = new LLVMValueRef[indices.length];
        for (int i = 0; i < indices.length; i++) newIndices[i] = constant(indices[i]);
        return buildGEP(builder, pointer, newIndices, name);
    }

    public static LLVMValueRef buildLoad(LLVMBuilderRef builder, LLVMValueRef pointer) {
        return LLVMBuildLoad(builder, pointer, "");
    }

    public static LLVMValueRef buildGEP(LLVMBuilderRef builder, LLVMValueRef pointer, String name, int... indices) {
        return buildGEP(builder, pointer, indices, name);
    }

    public static LLVMValueRef buildAllocaInFirstBlock(LLVMValueRef function, LLVMTypeRef type, String name) {
        var block = LLVMGetFirstBasicBlock(function);
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, block);
        var result = LLVMBuildAlloca(builder, type, name);
        LLVMDisposeBuilder(builder);
        return result;
    }

    // load e* from e*, type: e
    // load [n x e*]* from [n x e]*, type: Vectorized
    public static LLVMValueRef loadPtr(Type type, LLVMValueRef function, LLVMValueRef value) {
        if (type instanceof VectorizedType) {
            var result = buildAllocaInFirstBlock(function, type.withInnerPtrInLLVM(), "");
            appendForLoop(function, 0, ((VectorizedType) type).vectorizedLength(), "decl_load",
                    (bodyBuilder, i) -> {
                        var from = buildGEP(bodyBuilder, value, "", constant(0), i);
                        var to = buildGEP(bodyBuilder, result, "", constant(0), i);
                        LLVMBuildStore(bodyBuilder, from, to);
                        return null;
                    });
            return result;
        } else {
            return value;
        }
    }

    public static boolean isTerminal(LLVMValueRef instr) {
        return LLVMIsATerminatorInst(instr) != null;
    }

    // store e* to e*
    // store [n x e*]* to [n x e]*, type: Vectorized
    public static void storePtr(Type type, LLVMValueRef function, LLVMValueRef from, LLVMValueRef to) {
        if (type instanceof VectorizedType) {
            appendForLoop(function, 0, ((VectorizedType) type).vectorizedLength(), "", (builder, i) -> {
                var realFrom = buildLoad(builder, buildLoad(builder,
                        buildGEP(builder, from, "", constant(0), i)));
                var realTo = buildGEP(builder, to, "", constant(0), i);
                LLVMBuildStore(builder, realFrom, realTo);
                return null;
            });
        } else {
            var builder = LLVMCreateBuilder();
            LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
            LLVMBuildStore(builder, buildLoad(builder, from), to);
            LLVMDisposeBuilder(builder);
        }
    }

    // assign from e* to e*
    // assign from [n x e*]* to [n x e*]*
    public static void assign(Type type, LLVMValueRef function, LLVMValueRef xptr, LLVMValueRef yptr) {
        if (type instanceof VectorizedType) {
            appendForLoop(function, 0, ((VectorizedType) type).vectorizedLength(), "assign",
                    (bodyBuilder, i) -> {
                        var from = LLVMBuildLoad(bodyBuilder,
                                LLVMBuildLoad(bodyBuilder,
                                        buildGEP(bodyBuilder, yptr, "", constant(0), i),
                                        ""),
                                "");
                        var to = LLVMBuildLoad(bodyBuilder,
                                buildGEP(bodyBuilder, xptr, "", constant(0), i), "");
                        LLVMBuildStore(bodyBuilder, from, to);
                        return null;
                    });
        } else {
            var builder = LLVMCreateBuilder();
            LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
            LLVMBuildStore(builder, LLVMBuildLoad(builder, yptr, ""), xptr);
            LLVMDisposeBuilder(builder);
        }
    }

    // copy a new object on stack
    // copy from e* to e*
    // copy from [n x e*]* to [n x e*]*
    public static LLVMValueRef deepCopy(Type type, LLVMValueRef function, LLVMValueRef ptr) {
        var result = buildAllocaInFirstBlock(function, type.inLLVM(), "");
        storePtr(type, function, ptr, result);
        return loadPtr(type, function, result);
    }

    // e* to e*
    // [n x e*]* to e*
    public static LLVMValueRef builtInFuncPrequel(Type type, LLVMValueRef function, LLVMValueRef ptr) {
        var result = buildAllocaInFirstBlock(function, type.inLLVM(), "");
        storePtr(type, function, ptr, result);
        if (type instanceof VectorizedType) {
            var builder = LLVMCreateBuilder();
            LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
            var ans = buildGEP(builder, result, "", 0, 0);
            LLVMDisposeBuilder(builder);
            return ans;
        } else {
            return result;
        }
    }

    public static LLVMTypeRef builtInFuncType(Type returnType, Type[] parameterTypes) {
        var list = new ArrayList<LLVMTypeRef>();
        for (var parameterType : parameterTypes) {
            if (parameterType instanceof MatnxmType) {
                list.add(LLVMPointerType(((VectorizedType) parameterType).primitiveType().inLLVM(), 0));
                list.add(LLVMInt32Type());
                list.add(LLVMInt32Type());
            } else if (parameterType instanceof VectorizedType) {
                list.add(LLVMPointerType(((VectorizedType) parameterType).primitiveType().inLLVM(), 0));
                list.add(LLVMInt32Type());
            } else {
                list.add(LLVMPointerType(parameterType.inLLVM(), 0));
            }
        }
        if (returnType instanceof VectorizedType) {
            list.add(LLVMPointerType(((VectorizedType) returnType).primitiveType().inLLVM(), 0));
        } else {
            list.add(LLVMPointerType(returnType.inLLVM(), 0));
        }
        var arr = new LLVMTypeRef[list.size()];
        list.toArray(arr);
        return LLVMFunctionType(LLVMVoidType(), new PointerPointer<>(arr), list.size(), 0);
    }

    public static LLVMValueRef[] constructBuiltInFuncParameters(Type[] types, LLVMValueRef[] values,
                                                                Type returnType, LLVMValueRef returnValue,
                                                                LLVMValueRef function) {
        var list = new ArrayList<LLVMValueRef>();
        for (int i = 0; i < types.length; i++) {
            var type = types[i];
            var value = builtInFuncPrequel(type, function, values[i]);
            if (type instanceof MatnxmType) {
                list.add(value);
                list.add(constant(((MatnxmType) type).getN()));
                list.add(constant(((MatnxmType) type).getM()));
            } else if (type instanceof VectorizedType) {
                list.add(value);
                list.add(constant(((VectorizedType) type).vectorizedLength()));
            } else {
                list.add(value);
            }
        }

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));

        if (returnType instanceof VectorizedType) {
            list.add(buildGEP(builder, returnValue, "", 0, 0));
        } else {
            list.add(returnValue);
        }
        var arr = new LLVMValueRef[list.size()];
        list.toArray(arr);
        LLVMDisposeBuilder(builder);
        return arr;
    }

    public static LLVMValueRef buildCastBoolToInt1(LLVMBuilderRef builder, LLVMValueRef value) {
        return LLVMBuildIntCast2(builder,
                LLVMBuildLoad(builder, value, ""), LLVMInt1Type(), 0, "");
    }

    public static void log(LLVMValueRef value) {
        System.out.println(LLVMPrintValueToString(value).getString());
    }

    public static void log(String tag, LLVMValueRef value) {
        System.out.println("[" + tag + "] " + LLVMPrintValueToString(value).getString());
    }

    public static void log(LLVMModuleRef module) {
        System.out.println(LLVMPrintModuleToString(module).getString());
    }
}

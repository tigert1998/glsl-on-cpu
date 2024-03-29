package ast.values;

import ast.exceptions.*;
import ast.types.*;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.*;
import java.util.function.*;

public class IvecnValue extends Value implements Vectorized, Indexed, Selected {
    public int[] values = null;

    public IvecnValue(int n) {
        this.type = IvecnType.fromN(n);
        values = new int[n];
    }

    public IvecnValue(IvecnType type, IntValue v) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++) values[i] = v.value;
    }

    public IvecnValue(IvecnType type, List<IntValue> values) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++)
            this.values[i] = values.get(i).value;
    }

    public int getN() {
        return values.length;
    }

    @Override
    public Value[] retrieve() {
        var result = new Value[getN()];
        for (int i = 0; i < result.length; i++)
            result[i] = new IntValue(values[i]);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ivec" + values.length + "(" + values[0]);
        for (int i = 1; i < values.length; i++) builder.append(", ").append(values[i]);
        builder.append(")");
        return new String(builder);
    }

    static public IvecnValue pointwise(IvecnValue x, Function<Integer, Integer> f) {
        IvecnValue result = new IvecnValue(x.getN());
        for (int i = 0; i < x.getN(); i++)
            result.values[i] = f.apply(x.values[i]);
        return result;
    }

    static public IvecnValue pointwise(IvecnValue x, IvecnValue y, BiFunction<Integer, Integer, Integer> f) {
        IvecnValue res = new IvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.values[i]);
        return res;
    }

    // for shl and shr
    static public IvecnValue pointwise(IvecnValue x, UvecnValue y, BiFunction<Integer, Long, Integer> f) {
        IvecnValue res = new IvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.values[i]);
        return res;
    }

    static public IvecnValue pointwise(IvecnValue x, IntValue y, BiFunction<Integer, Integer, Integer> f) {
        IvecnValue res = new IvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.value);
        return res;
    }

    // for shl and shr
    static public IvecnValue pointwise(IvecnValue x, UintValue y, BiFunction<Integer, Long, Integer> f) {
        IvecnValue res = new IvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.value);
        return res;
    }

    static public IvecnValue pointwise(IntValue x, IvecnValue y, BiFunction<Integer, Integer, Integer> f) {
        IvecnValue res = new IvecnValue(y.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.value, y.values[i]);
        return res;
    }

    @Override
    public Value valueAt(int i) throws InvalidIndexException {
        if (i < 0 || i >= getN()) throw InvalidIndexException.outOfRange();
        return new IntValue(values[i]);
    }

    @Override
    public Value select(String name) throws InvalidSelectionException {
        int[] indices = SwizzleUtility.swizzle(values.length, name);
        if (indices.length == 1) {
            try {
                return valueAt(indices[0]);
            } catch (InvalidIndexException ignore) {
                return null;
            }
        } else {
            var res = new IvecnValue(indices.length);
            for (int i = 0; i < indices.length; i++) res.values[i] = this.values[indices[i]];
            return res;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IvecnValue)) return false;
        var ivecn = (IvecnValue) obj;
        if (!ivecn.getType().equals(this.getType())) return false;
        for (int i = 0; i < getN(); i++) if (values[i] != ivecn.values[i]) return false;
        return true;
    }

    @Override
    public LLVMValueRef inLLVM() {
        return Vectorized.inLLVM(this);
    }

    @Override
    public LLVMValueRef ptrInLLVM(LLVMValueRef function) {
        return Vectorized.ptrInLLVM(this, function);
    }
}

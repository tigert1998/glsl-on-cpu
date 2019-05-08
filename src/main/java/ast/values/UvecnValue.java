package ast.values;

import ast.exceptions.*;
import ast.types.*;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import java.util.*;
import java.util.function.*;

public class UvecnValue extends Value implements Vectorized, Indexed, Selected {
    public long[] values;

    public UvecnValue(int n) {
        values = new long[n];
        type = UvecnType.fromN(n);
    }

    public UvecnValue(UvecnType type, UintValue v) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++) values[i] = v.value;
    }

    public UvecnValue(UvecnType type, List<UintValue> values) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++)
            this.values[i] = values.get(i).value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("uvec" + values.length + "(" + values[0]);
        for (int i = 1; i < values.length; i++) builder.append(", ").append(values[i]);
        builder.append(")");
        return new String(builder);
    }

    public int getN() {
        return values.length;
    }

    @Override
    public Value[] retrieve() {
        var result = new Value[getN()];
        for (int i = 0; i < result.length; i++)
            result[i] = new UintValue(values[i]);
        return result;
    }

    static public UvecnValue pointwise(UvecnValue x, Function<Long, Long> f) {
        UvecnValue result = new UvecnValue(x.getN());
        for (int i = 0; i < x.getN(); i++)
            result.values[i] = f.apply(x.values[i]);
        return result;
    }

    static public UvecnValue pointwise(UvecnValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.values[i]);
        return res;
    }

    // for shl and shr
    static public UvecnValue pointwise(UvecnValue x, IvecnValue y, BiFunction<Long, Integer, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.values[i]);
        return res;
    }

    static public UvecnValue pointwise(UvecnValue x, UintValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.value);
        return res;
    }

    // for shl and shr
    static public UvecnValue pointwise(UvecnValue x, IntValue y, BiFunction<Long, Integer, Long> f) {
        UvecnValue res = new UvecnValue(x.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.values[i], y.value);
        return res;
    }

    static public UvecnValue pointwise(UintValue x, UvecnValue y, BiFunction<Long, Long, Long> f) {
        UvecnValue res = new UvecnValue(y.getN());
        for (int i = 0; i < res.getN(); i++)
            res.values[i] = f.apply(x.value, y.values[i]);
        return res;
    }

    @Override
    public Value valueAt(int i) throws InvalidIndexException {
        if (i < 0 || i >= values.length) throw InvalidIndexException.outOfRange();
        return new UintValue(values[i]);
    }

    @Override
    public Value select(String name) throws InvalidSelectionException {
        int[] indices = SwizzleUtility.swizzle(values.length, name);
        var res = new UvecnValue(indices.length);
        for (int i = 0; i < indices.length; i++) res.values[i] = this.values[indices[i]];
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UvecnValue)) return false;
        var uvecn = (UvecnValue) obj;
        if (!uvecn.getType().equals(this.getType())) return false;
        for (int i = 0; i < getN(); i++) if (values[i] != uvecn.values[i]) return false;
        return true;
    }

    @Override
    public LLVMValueRef inLLVM() {
        return Vectorized.inLLVM(this);
    }
}

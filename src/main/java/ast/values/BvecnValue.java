package ast.values;

import ast.exceptions.*;
import ast.types.*;

import java.util.*;

public class BvecnValue extends Value implements Vectorized, Indexed, Selected {
    public boolean[] values = null;

    public int getN() {
        return values.length;
    }

    public BvecnValue(int n) {
        values = new boolean[n];
        type = BvecnType.fromN(n);
    }

    public BvecnValue(BvecnType type, BoolValue v) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++) values[i] = v.value;
    }

    public BvecnValue(BvecnType type, List<BoolValue> values) {
        this(type.getN());
        for (int i = 0; i < type.getN(); i++)
            this.values[i] = values.get(i).value;
    }

    @Override
    public Value[] retrieve() {
        var result = new Value[getN()];
        for (int i = 0; i < result.length; i++)
            result[i] = new BoolValue(values[i]);
        return result;
    }

    @Override
    public Value valueAt(int i) throws InvalidIndexException {
        if (i < 0 || i >= getN()) throw InvalidIndexException.outOfRange();
        return new BoolValue(values[i]);
    }

    @Override
    public Value select(String name) throws InvalidSelectionException {
        int[] indices = SwizzleUtility.swizzle(values.length, name);
        var res = new BvecnValue(indices.length);
        for (int i = 0; i < indices.length; i++) res.values[i] = this.values[indices[i]];
        return res;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("bvec" + values.length + "(" + values[0]);
        for (int i = 1; i < values.length; i++) builder.append(", ").append(values[i]);
        builder.append(")");
        return new String(builder);
    }
}

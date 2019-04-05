package ast.values;

import ast.types.*;

import java.util.*;

public class BvecnValue extends Value implements Vectorized {
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
}

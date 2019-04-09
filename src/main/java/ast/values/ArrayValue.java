package ast.values;

import ast.exceptions.InvalidIndexException;
import ast.types.*;

public class ArrayValue extends Value implements Indexed {
    public Value[] values = null;

    public ArrayValue(ArrayType type, Value[] values) {
        this.values = values;
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(type.toString());
        sb.append('(');
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i].toString());
            if (i < values.length - 1) sb.append(", ");
        }
        sb.append(")");
        return new String(sb);
    }

    @Override
    public Value valueAt(int i) throws InvalidIndexException {
        if (i < 0 || i >= values.length) throw InvalidIndexException.outOfRange();
        return values[i];
    }
}

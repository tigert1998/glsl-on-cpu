package ast.values;

import ast.exceptions.InvalidIndexException;
import ast.types.*;

public class ArrayValue extends Value implements Indexed {
    public Value[] values = null;

    public ArrayValue(ArrayType type, Value value) {
        this.type = type;
        this.values = new Value[type.getLength()];
        for (int i = 0; i < values.length; i++) values[i] = value;
    }

    public ArrayValue(ArrayType type, Value[] values) {
        this.type = type;
        this.values = values;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayValue)) return false;
        var arr = (ArrayValue) obj;
        if (!arr.getType().equals(this.getType())) return false;
        for (int i = 0; i < values.length; i++) {
            if (!arr.values[i].equals(values[i])) return false;
        }
        return true;
    }
}

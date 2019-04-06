package ast.values;

import ast.types.*;

public class StructValue extends Value {
    public Value[] values;

    StructValue(StructType type, Value[] values) {
        this.values = values;
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < values.length; i++) {
            var field = ((StructType) type).getFieldInfo(i);
            sb.append('.').append(field.id).append(" = ");
            sb.append(values[i]);
            if (i < values.length - 1) sb.append(", ");
        }
        sb.append("}");
        return new String(sb);
    }
}

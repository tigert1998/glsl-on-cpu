package ast.values;

public class UvecnValue extends Value {
    public Long[] value = null;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("uvec" + value.length + "(" + value[0]);
        for (int i = 1; i < value.length; i++) builder.append(", ").append(value[i]);
        builder.append(")");
        return new String(builder);
    }
}

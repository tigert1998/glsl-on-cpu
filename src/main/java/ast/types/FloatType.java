package ast.types;

public class FloatType extends Type {
    public static FloatType FLOAT_TYPE = new FloatType();

    @Override
    public boolean equals(Object obj) {
        return FloatType.class == obj.getClass();
    }
}

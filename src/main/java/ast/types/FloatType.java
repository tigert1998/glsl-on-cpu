package ast.types;

public class FloatType extends Type {
    public static FloatType TYPE = new FloatType();

    @Override
    public boolean equals(Object obj) {
        return FloatType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "float";
    }
}

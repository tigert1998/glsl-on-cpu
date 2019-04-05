package ast.types;

public class UintType extends Type {
    public static UintType TYPE = new UintType();

    @Override
    public boolean equals(Object obj) {
        return UintType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "uint";
    }

    @Override
    public Type collapse() {
        return TYPE;
    }
}

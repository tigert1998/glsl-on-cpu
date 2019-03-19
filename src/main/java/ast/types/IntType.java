package ast.types;

public class IntType extends Type {
    public static IntType TYPE = new IntType();

    @Override
    public boolean equals(Object obj) {
        return IntType.class == obj.getClass();
    }

    @Override
    public String toString() {
        return "int";
    }
}

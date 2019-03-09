package ast.types;

public class IntType extends Type {
    public static IntType INT_TYPE = new IntType();

    @Override
    public boolean equals(Object obj) {
        return IntType.class == obj.getClass();
    }
}

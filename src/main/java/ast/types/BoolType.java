package ast.types;

public class BoolType extends Type {
    public static BoolType BOOL_TYPE = new BoolType();

    @Override
    public boolean equals(Object obj) {
        return BoolType.class == obj.getClass();
    }
}

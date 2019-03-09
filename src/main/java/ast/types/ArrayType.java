package ast.types;

public class ArrayType extends Type {
    private Type type;
    private int length;

    public ArrayType(Type type, int length) {
        this.type = type;
        this.length = length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != ArrayType.class) return false;
        ArrayType arrObj = (ArrayType) obj;
        return arrObj.length == this.length && arrObj.type.equals(this.type);
    }
}

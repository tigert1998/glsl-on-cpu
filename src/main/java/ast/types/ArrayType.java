package ast.types;

public class ArrayType extends Type {
    private Type type;
    private int length;
    private boolean lengthUnknown;

    public ArrayType(Type type, int length) {
        this.type = type;
        this.length = length;
        lengthUnknown = false;
    }

    public ArrayType(Type type) {
        this.type = type;
        lengthUnknown = true;
    }

    public Type getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public boolean isLengthUnknown() {
        return lengthUnknown;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayType)) return false;
        ArrayType arrObj = (ArrayType) obj;
        if (!arrObj.type.equals(this.type)) return false;
        if (arrObj.lengthUnknown || this.lengthUnknown) return true;
        return arrObj.length == this.length;
    }

    @Override
    public String toString() {
        if (lengthUnknown) return type + "[]";
        return type + "[" + length + "]";
    }

    public Type collapse() {
        return type;
    }
}

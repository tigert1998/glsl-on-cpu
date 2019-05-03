package ast.types;

import ast.values.*;

public class ArrayType extends Type implements IndexedType {
    private Type type;
    private int length;
    private boolean lengthUnknown;

    @Override
    public Type elementType() {
        return type;
    }

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

    @Override
    public int getN() {
        return length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        lengthUnknown = false;
        this.length = length;
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

    @Override
    public ArrayValue getDefaultValue() {
        return new ArrayValue(this, type.getDefaultValue());
    }
}

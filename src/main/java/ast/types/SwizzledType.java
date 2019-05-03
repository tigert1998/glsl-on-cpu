package ast.types;

public interface SwizzledType extends IndexedType, VectorizedType {
    int getN();

    SwizzledType changeN(int n);
}

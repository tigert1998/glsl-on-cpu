package ast.types;

public interface IndexedType {
    Type elementType();

    int getN();
}

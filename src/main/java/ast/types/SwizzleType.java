package ast.types;

public interface SwizzleType {
    int getN();

    SwizzleType changeN(int n);
}

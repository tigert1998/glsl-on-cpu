package ast.types;

public class UvecnType extends Type {
    private int n;

    public UvecnType(int n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        if (UvecnType.class != obj.getClass()) return false;
        return ((UvecnType) obj).n == this.n;
    }
}

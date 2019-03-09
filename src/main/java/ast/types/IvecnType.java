package ast.types;

public class IvecnType extends Type {
    private int n;

    public IvecnType(int n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        if (IvecnType.class != obj.getClass()) return false;
        return ((IvecnType) obj).n == this.n;
    }
}

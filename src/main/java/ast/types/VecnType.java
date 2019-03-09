package ast.types;

public class VecnType extends Type {
    private int n;

    public VecnType(int n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        if (VecnType.class != obj.getClass()) return false;
        return ((VecnType) obj).n == this.n;
    }
}

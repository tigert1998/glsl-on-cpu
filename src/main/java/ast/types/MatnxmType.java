package ast.types;

public class MatnxmType extends Type {
    private int n, m;

    public MatnxmType(int n, int m) {
        this.n = n;
        this.m = m;
    }

    @Override
    public boolean equals(Object obj) {
        if (MatnxmType.class != obj.getClass()) return false;
        MatnxmType matObj = (MatnxmType) obj;
        return matObj.n == this.n && matObj.m == this.m;
    }
}

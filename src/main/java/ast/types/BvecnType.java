package ast.types;

public class BvecnType extends Type {
    private int n;

    // prevent multiple new
    private static BvecnType BVEC2 = new BvecnType(2);
    private static BvecnType BVEC3 = new BvecnType(3);
    private static BvecnType BVEC4 = new BvecnType(4);

    static public BvecnType fromN(int n) {
        switch (n) {
            case 2:
                return BVEC2;
            case 3:
                return BVEC3;
            case 4:
                return BVEC4;
            default:
                return null;
        }
    }

    private BvecnType(int n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        if (BvecnType.class != obj.getClass()) return false;
        return ((BvecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "bvec" + n;
    }
}

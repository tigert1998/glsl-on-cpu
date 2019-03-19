package ast.types;

public class VecnType extends Type {
    private int n;

    private static VecnType VEC2 = new VecnType(2);
    private static VecnType VEC3 = new VecnType(3);
    private static VecnType VEC4 = new VecnType(4);

    public VecnType(int n) {
        this.n = n;
    }

    static public VecnType fromN(int n) {
        switch (n) {
            case 2:
                return VEC2;
            case 3:
                return VEC3;
            case 4:
                return VEC4;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (VecnType.class != obj.getClass()) return false;
        return ((VecnType) obj).n == this.n;
    }

    @Override
    public String toString() {
        return "vec" + n;
    }
}

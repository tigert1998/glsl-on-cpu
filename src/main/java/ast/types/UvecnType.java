package ast.types;

public class UvecnType extends Type {
    private int n;

    private static UvecnType UVEC2 = new UvecnType(2);
    private static UvecnType UVEC3 = new UvecnType(3);
    private static UvecnType UVEC4 = new UvecnType(4);

    private UvecnType(int n) {
        this.n = n;
    }

    static public UvecnType fromN(int n) {
        switch (n) {
            case 2:
                return UVEC2;
            case 3:
                return UVEC3;
            case 4:
                return UVEC4;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (UvecnType.class != obj.getClass()) return false;
        return ((UvecnType) obj).n == this.n;
    }
}

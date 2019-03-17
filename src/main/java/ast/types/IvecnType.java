package ast.types;

public class IvecnType extends Type {
    private int n;

    // prevent multiple new
    private static IvecnType IVEC2 = new IvecnType(2);
    private static IvecnType IVEC3 = new IvecnType(3);
    private static IvecnType IVEC4 = new IvecnType(4);

    private IvecnType(int n) {
        this.n = n;
    }

    static public IvecnType fromN(int n) {
        switch (n) {
            case 2:
                return IVEC2;
            case 3:
                return IVEC3;
            case 4:
                return IVEC4;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (IvecnType.class != obj.getClass()) return false;
        return ((IvecnType) obj).n == this.n;
    }
}

package ast.types;

public class MatnxmType extends Type {
    private int n, m;
    private static MatnxmType[][] predefinedTypes = new MatnxmType[3][3];

    static {
        for (int i = 2; i <= 4; i++)
            for (int j = 2; j <= 4; j++)
                predefinedTypes[i - 2][j - 2] = new MatnxmType(i, j);
    }

    private MatnxmType(int n, int m) {
        this.n = n;
        this.m = m;
    }

    public static MatnxmType fromNM(int n, int m) {
        return predefinedTypes[n - 2][m - 2];
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    @Override
    public boolean equals(Object obj) {
        if (MatnxmType.class != obj.getClass()) return false;
        MatnxmType matObj = (MatnxmType) obj;
        return matObj.n == this.n && matObj.m == this.m;
    }

    @Override
    public String toString() {
        if (n == m) return "mat" + n;
        return "mat" + n + "x" + m;
    }
}

package ast.values;

import ast.types.MatnxmType;

import java.util.function.*;

public class MatnxmValue extends Value {
    public Float[][] value = null;

    public MatnxmValue(int n, int m) {
        this.type = MatnxmType.fromNM(n, m);
        value = new Float[n][m];
    }

    public MatnxmValue map(Function<Float, Float> f) {
        int n = ((MatnxmType) type).getN(), m = ((MatnxmType) type).getM();
        MatnxmValue result = new MatnxmValue(n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                result.value[i][j] = f.apply(value[i][j]);
        return result;
    }
}

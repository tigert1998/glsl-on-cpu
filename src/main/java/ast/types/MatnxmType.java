package ast.types;

import ast.exceptions.ConstructionFailedException;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;
import java.util.*;

public class MatnxmType extends Type implements IndexedType, IncreasableType, VectorizedType {
    private int n, m;

    // prevent multiple new
    private static MatnxmType[][] predefinedTypes = new MatnxmType[3][3];
    private static MatnxmValue[][] zeros = new MatnxmValue[3][3];
    private static MatnxmValue[][] ones = new MatnxmValue[3][3];

    static {
        for (int i = 2; i <= 4; i++)
            for (int j = 2; j <= 4; j++) {
                predefinedTypes[i - 2][j - 2] = new MatnxmType(i, j);
                zeros[i - 2][j - 2] = new MatnxmValue(predefinedTypes[i - 2][j - 2],
                        FloatType.TYPE.zero(), false);
                ones[i - 2][j - 2] = new MatnxmValue(predefinedTypes[i - 2][j - 2],
                        FloatType.TYPE.one(), false);
            }
    }

    @Override
    public Type elementType() {
        return VecnType.fromN(m);
    }

    @Override
    public Type primitiveType() {
        return FloatType.TYPE;
    }

    private MatnxmType(int n, int m) {
        this.n = n;
        this.m = m;
    }

    public static MatnxmType fromNM(int n, int m) {
        return predefinedTypes[n - 2][m - 2];
    }

    public static MatnxmType fromText(String text) {
        if (text.length() == 4) {
            // matn
            int n = text.charAt(text.length() - 1) - '0';
            return fromNM(n, n);
        } else {
            int n = text.charAt(3) - '0', m = text.charAt(5) - '0';
            return fromNM(n, m);
        }
    }

    @Override
    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    @Override
    public int vectorizedLength() {
        return n * m;
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

    @Override
    public MatnxmValue zero() {
        return zeros[n - 2][m - 2];
    }

    @Override
    public MatnxmValue one() {
        return ones[n - 2][m - 2];
    }

    @Override
    public Value construct(Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        boolean hasMat = false;
        for (var value : values) if (value instanceof MatnxmValue) hasMat = true;
        if (hasMat && values.length >= 2)
            throw ConstructionFailedException.matrixFromMatrix();
        if (hasMat) return new MatnxmValue(this, (MatnxmValue) values[0]);
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new MatnxmValue(this, FloatType.TYPE.construct(values), true);
        }
        List<FloatValue> valueList = flattenThenConvertToFloatValue(values);
        if (valueList.size() < this.getN() * this.getM())
            throw ConstructionFailedException.notEnoughData();
        return new MatnxmValue(this, valueList);
    }

    @Override
    public LLVMTypeRef inLLVM() {
        return LLVMArrayType(FloatType.TYPE.inLLVM(), getN() * getM());
    }

    @Override
    public LLVMTypeRef withInnerPtrInLLVM() {
        return VectorizedType.withInnerPtrInLLVM(this);
    }
}

package ast.types;

public abstract class Type {
    static public boolean isScalar(Type type) {
        return type instanceof BoolType
                || type instanceof IntType
                || type instanceof UintType
                || type instanceof FloatType;
    }

    static public boolean isVector(Type type) {
        return type instanceof BvecnType
                || type instanceof IvecnType
                || type instanceof UvecnType
                || type instanceof VecnType;
    }

    static public boolean isIntegral(Type type) {
        return type instanceof IntType
                || type instanceof UintType
                || type instanceof IvecnType
                || type instanceof UvecnType;
    }

    static public boolean isBoolean(Type type) {
        return type instanceof BoolType
                || type instanceof BvecnType;
    }

    static public boolean isFloat(Type type) {
        return type instanceof FloatType
                || type instanceof VecnType
                || type instanceof MatnxmType;
    }

    static public boolean isBasic(Type type) {
        return isScalar(type) || isVector(type) || type instanceof MatnxmType;
    }

    abstract public Type collapse();
}

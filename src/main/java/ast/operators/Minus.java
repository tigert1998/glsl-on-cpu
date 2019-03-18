package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

public class Minus extends UnaryOperator {
    public static Minus OP = new Minus();

    @Override
    public boolean canBeApplied(Type type) {
        return type instanceof IntType
                || type instanceof UintType
                || type instanceof FloatType
                || type instanceof VecnType
                || type instanceof IvecnType
                || type instanceof UvecnType
                || type instanceof MatnxmType;
    }

    @Override
    public Value apply(Value value, Scope scope) throws NotLValueException, OperatorCannotBeAppliedException {
        var type = value.getType();
        if (type instanceof IntType) {
            return new IntValue(-((IntValue) value).value);
        } else if (type instanceof UintType) {
            return new UintValue(-((UintValue) value).value);
        } else if (type instanceof FloatType) {
            return new FloatValue(-((FloatValue) value).value);
        } else if (type instanceof VecnType) {
            return ((VecnValue) value).map(x -> -x);
        } else if (type instanceof IvecnType) {
            return ((IvecnValue) value).map(x -> -x);
        } else if (type instanceof MatnxmType) {
            return ((MatnxmValue) value).map(x -> -x);
        } else
            throw new OperatorCannotBeAppliedException("-", type.toString());
    }
}

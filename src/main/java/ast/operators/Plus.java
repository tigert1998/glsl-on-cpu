package ast.operators;

import ast.Scope;
import ast.types.*;
import ast.values.Value;

public class Plus extends UnaryOperator {
    public static Plus OP = new Plus();

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
        if (!canBeApplied(value.getType()))
            throw new OperatorCannotBeAppliedException("+", value.getType().toString());
        return value;
    }
}

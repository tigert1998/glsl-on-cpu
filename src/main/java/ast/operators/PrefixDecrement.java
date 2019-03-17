package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

public class PrefixDecrement extends UnaryOperator {
    @Override
    public boolean canBeApplied(Type type) {
        return type == IntType.TYPE
                || type == UintType.TYPE
                || type == FloatType.TYPE
                || type instanceof VecnType
                || type instanceof IvecnType
                || type instanceof MatnxmType;
    }

    @Override
    public Value apply(Value value, Scope scope) throws NotLValueErrorException {
        if (value.getId() == null || scope.variables.get(value.getId()) == null)
            throw new NotLValueErrorException();
        var type = value.getType();
        if (type instanceof IntType) {
            ((IntValue) value).value -= 1;
        } else if (type instanceof UintType) {
            ((UintValue) value).value -= 1;
        } else if (type instanceof FloatType) {
            ((FloatValue) value).value -= 1;
        } else if (type instanceof VecnType) {
            ((VecnValue) value).map(x -> x - 1);
        } else if (type instanceof IvecnType) {
            ((IvecnValue) value).map(x -> x - 1);
        } else return null;
        scope.variables.put(value.getId(), value);
        return value;
    }
}

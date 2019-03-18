package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

public class PrefixIncrement extends UnaryOperator {
    @Override
    public boolean canBeApplied(Type type) {
        return type instanceof IntType
                || type instanceof UintType
                || type instanceof FloatType
                || type instanceof VecnType
                || type instanceof IvecnType
                || type instanceof MatnxmType;
    }

    @Override
    public Value apply(Value value, Scope scope) throws NotLValueException, OperatorCannotBeAppliedException {
        checkIsLValue(value, scope);
        String id = value.getId();
        value = increment(value);
        value.setId(id);
        // still a left value
        scope.variables.put(id, value);
        return value;
    }
}
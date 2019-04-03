package ast.operators;

import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class PrefixDecrement extends Operator implements UnaryOperator {
    static public PrefixDecrement OP = new PrefixDecrement();

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
    public Value apply(Value value, Scope scope) throws SyntaxErrorException {
        UnaryOperator.checkIsLValue(value, scope);
        String id = value.getId();
        value = UnaryOperator.decrement(value);
        value.setId(id);
        // still a left value
        scope.variables.put(id, value);
        return value;
    }

    @Override
    public String toString() {
        return "--";
    }
}

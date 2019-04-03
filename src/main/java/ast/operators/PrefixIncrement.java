package ast.operators;

import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class PrefixIncrement extends Operator implements UnaryOperator {
    static public PrefixIncrement OP = new PrefixIncrement();

    @Override
    public boolean canBeApplied(Type type) {
        return type instanceof IntType
                || type instanceof UintType
                || type instanceof FloatType
                || type instanceof VecnType
                || type instanceof IvecnType
                || type instanceof MatnxmType;
    }

    // make sure value is a l-value
    @Override
    public Value apply(Value value, Scope scope) {
        String id = value.getId();
        value = UnaryOperator.increment(value);
        value.setId(id);
        // still a left value
        scope.variables.put(id, value);
        return value;
    }

    @Override
    public String toString() {
        return "++";
    }
}
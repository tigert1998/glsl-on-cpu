package ast.operators;

import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class PostfixDecrement extends Operator implements UnaryOperator {
    static public PostfixDecrement OP = new PostfixDecrement();

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
        Value oldValue = value;
        oldValue.setId(null);
        // becomes a rvalue
        value = UnaryOperator.decrement(value);
        value.setId(id);
        scope.variables.put(id, value);
        return oldValue;
    }

    @Override
    public String toString() {
        return "--";
    }
}
package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

public class PostfixIncrement extends Operator implements UnaryOperator {
    static public PostfixIncrement OP = new PostfixIncrement();

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
        UnaryOperator.checkIsLValue(value, scope);
        String id = value.getId();
        Value oldValue = value;
        oldValue.setId(null);
        // becomes a rvalue
        value = UnaryOperator.increment(value);
        value.setId(id);
        scope.variables.put(id, value);
        return oldValue;
    }

    @Override
    public String toString() {
        return "++";
    }
}
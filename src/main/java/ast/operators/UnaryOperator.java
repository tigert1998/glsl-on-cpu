package ast.operators;

import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public interface UnaryOperator {
    boolean canBeApplied(Type type);

    Value apply(Value value, Scope scope)
            throws SyntaxErrorException;

    static Value increment(Value value) throws OperatorCannotBeAppliedException {
        // removes id information
        var type = value.getType();
        if (type instanceof IntType) {
            return new IntValue(((IntValue) value).value + 1);
        } else if (type instanceof UintType) {
            return new UintValue(((UintValue) value).value + 1);
        } else if (type instanceof FloatType) {
            return new FloatValue(((FloatValue) value).value + 1);
        } else if (type instanceof VecnType) {
            return ((VecnValue) value).map(x -> x + 1);
        } else if (type instanceof IvecnType) {
            return ((IvecnValue) value).map(x -> x + 1);
        } else if (type instanceof MatnxmType) {
            return ((MatnxmValue) value).map(x -> x + 1);
        } else
            throw new OperatorCannotBeAppliedException(PrefixIncrement.OP, type);
    }

    static Value decrement(Value value) throws OperatorCannotBeAppliedException {
        // removes id information
        var type = value.getType();
        if (type instanceof IntType) {
            return new IntValue(((IntValue) value).value - 1);
        } else if (type instanceof UintType) {
            return new UintValue(((UintValue) value).value - 1);
        } else if (type instanceof FloatType) {
            return new FloatValue(((FloatValue) value).value - 1);
        } else if (type instanceof VecnType) {
            return ((VecnValue) value).map(x -> x - 1);
        } else if (type instanceof IvecnType) {
            return ((IvecnValue) value).map(x -> x - 1);
        } else if (type instanceof MatnxmType) {
            return ((MatnxmValue) value).map(x -> x - 1);
        } else
            throw new OperatorCannotBeAppliedException(PrefixDecrement.OP, type);
    }

    static void checkIsLValue(Value value, Scope scope) throws SyntaxErrorException {
        if (value.getId() == null || scope.variables.get(value.getId()) == null)
            throw SyntaxErrorException.lvalueRequired();
    }
}

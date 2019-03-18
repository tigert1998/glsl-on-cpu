package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

public abstract class UnaryOperator extends Operator {
    abstract public boolean canBeApplied(Type type);

    abstract public Value apply(Value value, Scope scope) throws NotLValueException, OperatorCannotBeAppliedException;

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
            throw new OperatorCannotBeAppliedException("operator++", type.toString());
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
            throw new OperatorCannotBeAppliedException("operator--", type.toString());
    }

    static void checkIsLValue(Value value, Scope scope) throws NotLValueException {
        if (value.getId() == null || scope.variables.get(value.getId()) == null)
            throw new NotLValueException();
    }
}

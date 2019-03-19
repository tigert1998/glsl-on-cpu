package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

import java.util.function.BiFunction;

public class Plus extends Operator implements UnaryOperator, BinaryOperator {
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
            throw new OperatorCannotBeAppliedException("+", value.getType());
        return value;
    }

    @Override
    public boolean canBeApplied(Type type1, Type type2) {
        boolean ss = (type1 instanceof IntType && type2 instanceof IntType)
                || (type1 instanceof UintType && type2 instanceof UintType)
                || (type1 instanceof FloatType && type2 instanceof FloatType);
        if (ss) return true;
        boolean vv = (type1 instanceof IvecnType && type2 instanceof IvecnType)
                || (type1 instanceof UvecnType && type2 instanceof UvecnType)
                || (type1 instanceof VecnType && type2 instanceof VecnType)
                || (type1 instanceof MatnxmType && type2 instanceof MatnxmType);
        if (vv) return true;

        BiFunction<Type, Type, Boolean> vsChecker = (Type v, Type s) ->
                (v instanceof IvecnType && s instanceof IntType)
                        || (v instanceof UvecnType && s instanceof UintType)
                        || (v instanceof VecnType && s instanceof FloatType);

        return vsChecker.apply(type1, type2) && vsChecker.apply(type2, type1);
    }

    @Override
    public Value apply(Value value1, Value value2, Scope scope)
            throws NotLValueException, OperatorCannotBeAppliedException {
        Type type1 = value1.getType(), type2 = value2.getType();
        if (!canBeApplied(type1, type2))
            throw new OperatorCannotBeAppliedException("+", type1, type2);
        if (type1.equals(type2)) {
            if (type1 instanceof IntType) {
                return new IntValue(((IntValue) value1).value + ((IntValue) value2).value);
            } else if (type1 instanceof UintType) {
                return new UintValue(((UintValue) value1).value + ((UintValue) value2).value);
            } else if (type1 instanceof FloatType) {
                return new FloatValue(((FloatValue) value1).value + ((FloatValue) value2).value);
            } else if (type1 instanceof IvecnType) {

            }
        }
        return null;
    }
}

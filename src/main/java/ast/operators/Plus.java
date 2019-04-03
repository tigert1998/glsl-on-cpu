package ast.operators;

import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

import java.util.function.*;

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

    // always needs to check canBeApplied to make sure it returns correct answer
    @Override
    public Value apply(Value value, Scope scope) {
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
        if (vv && type1.equals(type2)) return true;

        BiFunction<Type, Type, Boolean> vsChecker = (Type v, Type s) ->
                (v instanceof IvecnType && s instanceof IntType)
                        || (v instanceof UvecnType && s instanceof UintType)
                        || (v instanceof VecnType && s instanceof FloatType)
                        || (v instanceof MatnxmType && s instanceof FloatType);

        return vsChecker.apply(type1, type2) || vsChecker.apply(type2, type1);
    }

    // always needs to check canBeApplied to make sure it returns correct answer
    @Override
    public Value apply(Value value1, Value value2, Scope scope) {
        Type type1 = value1.getType(), type2 = value2.getType();
        if (type1.equals(type2)) {
            if (type1 instanceof IntType) {
                return new IntValue(((IntValue) value1).value + ((IntValue) value2).value);
            } else if (type1 instanceof UintType) {
                return new UintValue(((UintValue) value1).value + ((UintValue) value2).value);
            } else if (type1 instanceof FloatType) {
                return new FloatValue(((FloatValue) value1).value + ((FloatValue) value2).value);
            } else if (type1 instanceof IvecnType) {
                return IvecnValue.applyFunction((IvecnValue) value1, (IvecnValue) value2, (x, y) -> x + y);
            } else if (type1 instanceof UvecnType) {
                return UvecnValue.applyFunction((UvecnValue) value1, (UvecnValue) value2, (x, y) -> x + y);
            } else if (type1 instanceof VecnType) {
                return VecnValue.applyFunction((VecnValue) value1, (VecnValue) value2, (x, y) -> x + y);
            } else {
                // matnxm
                return MatnxmValue.applyFunction((MatnxmValue) value1, (MatnxmValue) value2, (x, y) -> x + y);
            }
        } else {
            BiFunction<Value, Value, Value> vsFunc = (Value v, Value s) -> {
                var type = v.getType();
                if (type instanceof IvecnType) {
                    return IvecnValue.applyFunction((IvecnValue) v, (IntValue) s, (x, y) -> x + y, false);
                } else if (type instanceof UvecnType) {
                    return UvecnValue.applyFunction((UvecnValue) v, (UintValue) s, (x, y) -> x + y, false);
                } else if (type instanceof VecnType) {
                    return VecnValue.applyFunction((VecnValue) v, (FloatValue) s, (x, y) -> x + y, false);
                } else {
                    // matnxm
                    return MatnxmValue.applyFunction((MatnxmValue) v, (FloatValue) s, (x, y) -> x + y, false);
                }
            };
            return type1 instanceof IntType || type1 instanceof UintType || type1 instanceof FloatType
                    ? vsFunc.apply(value2, value1) : vsFunc.apply(value1, value2);
        }
    }

    @Override
    public String toString() {
        return "+";
    }
}

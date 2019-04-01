package ast.operators;

import ast.Scope;
import ast.types.*;
import ast.values.*;

import java.util.function.*;

public class Mod extends Operator implements BinaryOperator {
    public static Mod OP = new Mod();

    private Value applyFunctionOnVectorScalar(Value v, Value s, boolean flipped) {
        var type = v.getType();
        if (type instanceof IvecnType) {
            return IvecnValue.applyFunction((IvecnValue) v, (IntValue) s, (x, y) -> x % y, flipped);
        } else {
            return UvecnValue.applyFunction((UvecnValue) v, (UintValue) s, (x, y) -> x % y, flipped);
        }
    }

    @Override
    public boolean canBeApplied(Type type1, Type type2) {
        boolean ss = (type1 instanceof IntType && type2 instanceof IntType)
                || (type1 instanceof UintType && type2 instanceof UintType);
        if (ss) return true;
        boolean vv = (type1 instanceof IvecnType && type2 instanceof IvecnType)
                || (type1 instanceof UvecnType && type2 instanceof UvecnType);
        if (vv && type1.equals(type2)) return true;

        BiFunction<Type, Type, Boolean> vsChecker = (Type v, Type s) ->
                (v instanceof IvecnType && s instanceof IntType)
                        || (v instanceof UvecnType && s instanceof UintType);

        return vsChecker.apply(type1, type2) || vsChecker.apply(type2, type1);
    }

    @Override
    public Value apply(Value value1, Value value2, Scope scope) throws NotLValueException, OperatorCannotBeAppliedException {
        Type type1 = value1.getType(), type2 = value2.getType();
        if (!canBeApplied(type1, type2))
            throw new OperatorCannotBeAppliedException(this, type1, type2);
        if (type1.equals(type2)) {
            if (type1 instanceof IntType) {
                return new IntValue(((IntValue) value1).value % ((IntValue) value2).value);
            } else if (type1 instanceof UintType) {
                return new UintValue(((UintValue) value1).value % ((UintValue) value2).value);
            } else if (type1 instanceof IvecnType) {
                return IvecnValue.applyFunction((IvecnValue) value1, (IvecnValue) value2, (x, y) -> x % y);
            } else {
                return UvecnValue.applyFunction((UvecnValue) value1, (UvecnValue) value2, (x, y) -> x % y);
            }
        } else {
            return type1 instanceof IntType || type1 instanceof UintType
                    ? applyFunctionOnVectorScalar(value2, value1, true) :
                    applyFunctionOnVectorScalar(value1, value2, false);
        }
    }

    @Override
    public String toString() {
        return "%";
    }
}

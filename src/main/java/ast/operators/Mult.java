package ast.operators;

import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

import java.util.function.*;

public class Mult extends Operator implements BinaryOperator {
    public static Mult OP = new Mult();

    @Override
    public boolean canBeApplied(Type type1, Type type2) {
        return Plus.OP.canBeApplied(type1, type2);
    }

    @Override
    public Value apply(Value value1, Value value2, Scope scope) throws SyntaxErrorException {
        Type type1 = value1.getType(), type2 = value2.getType();
        if (!canBeApplied(type1, type2))
            throw new OperatorCannotBeAppliedException(this, type1, type2);
        if (type1.equals(type2)) {
            if (type1 instanceof IntType) {
                return new IntValue(((IntValue) value1).value * ((IntValue) value2).value);
            } else if (type1 instanceof UintType) {
                return new UintValue(((UintValue) value1).value * ((UintValue) value2).value);
            } else if (type1 instanceof FloatType) {
                return new FloatValue(((FloatValue) value1).value * ((FloatValue) value2).value);
            } else if (type1 instanceof IvecnType) {
                return IvecnValue.applyFunction((IvecnValue) value1, (IvecnValue) value2, (x, y) -> x * y);
            } else if (type1 instanceof UvecnType) {
                return UvecnValue.applyFunction((UvecnValue) value1, (UvecnValue) value2, (x, y) -> x * y);
            } else if (type1 instanceof VecnType) {
                return VecnValue.applyFunction((VecnValue) value1, (VecnValue) value2, (x, y) -> x * y);
            } else {
                // matnxm
                return MatnxmValue.applyFunction((MatnxmValue) value1, (MatnxmValue) value2, (x, y) -> x * y);
            }
        } else {
            BiFunction<Value, Value, Value> vsFunc = (Value v, Value s) -> {
                var type = v.getType();
                if (type instanceof IvecnType) {
                    return IvecnValue.applyFunction((IvecnValue) v, (IntValue) s, (x, y) -> x * y, false);
                } else if (type instanceof UvecnType) {
                    return UvecnValue.applyFunction((UvecnValue) v, (UintValue) s, (x, y) -> x * y, false);
                } else if (type instanceof VecnType) {
                    return VecnValue.applyFunction((VecnValue) v, (FloatValue) s, (x, y) -> x * y, false);
                } else {
                    // matnxm
                    return MatnxmValue.applyFunction((MatnxmValue) v, (FloatValue) s, (x, y) -> x * y, false);
                }
            };
            return type1 instanceof IntType || type1 instanceof UintType || type1 instanceof FloatType
                    ? vsFunc.apply(value2, value1) : vsFunc.apply(value1, value2);
        }
    }

    @Override
    public String toString() {
        return "*";
    }
}

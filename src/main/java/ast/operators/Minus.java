package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

import java.util.function.*;

public class Minus extends Operator implements UnaryOperator, BinaryOperator {
    public static Minus OP = new Minus();

    private Value applyFunctionOnVectorScalar(Value v, Value s, boolean flipped) {
        var type = v.getType();
        if (type instanceof IvecnType) {
            return IvecnValue.applyFunction((IvecnValue) v, (IntValue) s, (x, y) -> x - y, flipped);
        } else if (type instanceof UvecnType) {
            return UvecnValue.applyFunction((UvecnValue) v, (UintValue) s, (x, y) -> x - y, flipped);
        } else if (type instanceof VecnType) {
            return VecnValue.applyFunction((VecnValue) v, (FloatValue) s, (x, y) -> x - y, flipped);
        } else {
            // matnxm
            return MatnxmValue.applyFunction((MatnxmValue) v, (FloatValue) s, (x, y) -> x - y, flipped);
        }
    }

    @Override
    public boolean canBeApplied(Type type) {
        return Plus.OP.canBeApplied(type);
    }

    @Override
    public Value apply(Value value, Scope scope) throws NotLValueException, OperatorCannotBeAppliedException {
        var type = value.getType();
        if (type instanceof IntType) {
            return new IntValue(-((IntValue) value).value);
        } else if (type instanceof UintType) {
            return new UintValue(-((UintValue) value).value);
        } else if (type instanceof FloatType) {
            return new FloatValue(-((FloatValue) value).value);
        } else if (type instanceof VecnType) {
            return ((VecnValue) value).map(x -> -x);
        } else if (type instanceof IvecnType) {
            return ((IvecnValue) value).map(x -> -x);
        } else if (type instanceof MatnxmType) {
            return ((MatnxmValue) value).map(x -> -x);
        } else
            throw new OperatorCannotBeAppliedException(this, type);
    }

    @Override
    public boolean canBeApplied(Type type1, Type type2) {
        return Plus.OP.canBeApplied(type1, type2);
    }

    @Override
    public Value apply(Value value1, Value value2, Scope scope)
            throws NotLValueException, OperatorCannotBeAppliedException {
        Type type1 = value1.getType(), type2 = value2.getType();
        if (!canBeApplied(type1, type2))
            throw new OperatorCannotBeAppliedException(this, type1, type2);
        if (type1.equals(type2)) {
            if (type1 instanceof IntType) {
                return new IntValue(((IntValue) value1).value - ((IntValue) value2).value);
            } else if (type1 instanceof UintType) {
                return new UintValue(((UintValue) value1).value - ((UintValue) value2).value);
            } else if (type1 instanceof FloatType) {
                return new FloatValue(((FloatValue) value1).value - ((FloatValue) value2).value);
            } else if (type1 instanceof IvecnType) {
                return IvecnValue.applyFunction((IvecnValue) value1, (IvecnValue) value2, (x, y) -> x - y);
            } else if (type1 instanceof UvecnType) {
                return UvecnValue.applyFunction((UvecnValue) value1, (UvecnValue) value2, (x, y) -> x - y);
            } else if (type1 instanceof VecnType) {
                return VecnValue.applyFunction((VecnValue) value1, (VecnValue) value2, (x, y) -> x - y);
            } else {
                // matnxm
                return MatnxmValue.applyFunction((MatnxmValue) value1, (MatnxmValue) value2, (x, y) -> x - y);
            }
        } else {
            return type1 instanceof IntType || type1 instanceof UintType || type1 instanceof FloatType
                    ? applyFunctionOnVectorScalar(value2, value1, true) :
                    applyFunctionOnVectorScalar(value1, value2, false);
        }
    }

    @Override
    public String toString() {
        return "-";
    }
}

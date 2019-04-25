package ast.operators;

import ast.values.*;
import ast.types.*;

public class Less extends Operator implements BinaryOperator {
    public static Less OP = new Less();

    // == for values ==
    // scalar
    protected BoolValue apply(IntValue x, IntValue y) {
        return new BoolValue(x.value < y.value);
    }

    protected BoolValue apply(UintValue x, UintValue y) {
        return new BoolValue(x.value < y.value);
    }

    protected BoolValue apply(FloatValue x, FloatValue y) {
        return new BoolValue(x.value < y.value);
    }

    // == for types ==
    protected BoolType apply(IntType x, IntType y) {
        return BoolType.TYPE;
    }

    protected BoolType apply(UintType x, UintType y) {
        return BoolType.TYPE;
    }

    protected BoolType apply(FloatType x, FloatType y) {
        return BoolType.TYPE;
    }

    @Override
    public String toString() {
        return "<";
    }
}

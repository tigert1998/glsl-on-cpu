package ast.operators;

import ast.Scope;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class BitwiseNot extends Operator implements UnaryOperator {
    static public BitwiseNot OP = new BitwiseNot();

    @Override
    public boolean canBeApplied(Type type) {
        return type instanceof IntType
                || type instanceof UintType
                || type instanceof IvecnType
                || type instanceof UvecnType;
    }

    @Override
    public Value apply(Value value, Scope scope) throws SyntaxErrorException {
        var type = value.getType();
        if (type instanceof IntType) {
            return new IntValue(~((IntValue) value).value);
        } else if (type instanceof UintType) {
            return new UintValue(~((UintValue) value).value);
        } else if (type instanceof IvecnType) {
            return ((IvecnValue) value).map(x -> ~x);
        } else if (type instanceof UvecnType) {
            return ((IvecnValue) value).map(x -> ~x);
        } else
            throw new OperatorCannotBeAppliedException(this, type);
    }

    @Override
    public String toString() {
        return "~";
    }
}

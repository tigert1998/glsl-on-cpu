package ast.operators;

import ast.Scope;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class LogicalNot extends Operator implements UnaryOperator {
    static public LogicalNot OP = new LogicalNot();

    @Override
    public boolean canBeApplied(Type type) {
        return type instanceof BoolType;
    }

    // always needs to check canBeApplied to make sure it returns correct answer
    @Override
    public Value apply(Value value, Scope scope) {
        return new BoolValue(!((BoolValue) value).value);
    }

    @Override
    public String toString() {
        return "!";
    }
}

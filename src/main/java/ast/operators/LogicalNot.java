package ast.operators;

import ast.Scope;
import ast.types.*;
import ast.values.*;

public class LogicalNot extends Operator implements UnaryOperator {
    static public LogicalNot OP = new LogicalNot();

    @Override
    public boolean canBeApplied(Type type) {
        return type instanceof BoolType;
    }

    @Override
    public Value apply(Value value, Scope scope) throws NotLValueException, OperatorCannotBeAppliedException {
        var type = value.getType();
        if (canBeApplied(type)) {
            return new BoolValue(!((BoolValue) value).value);
        } else throw new OperatorCannotBeAppliedException(this, type);
    }

    @Override
    public String toString() {
        return "!";
    }
}

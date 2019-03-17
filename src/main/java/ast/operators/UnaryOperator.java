package ast.operators;

import ast.*;
import ast.types.*;
import ast.values.*;

public abstract class UnaryOperator extends Operator {
    abstract public boolean canBeApplied(Type type);

    abstract public Value apply(Value value, Scope scope) throws NotLValueErrorException;
}

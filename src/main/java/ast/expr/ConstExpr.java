package ast.expr;

import ast.values.*;

public class ConstExpr extends Expr {
    private Value value;

    public ConstExpr(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

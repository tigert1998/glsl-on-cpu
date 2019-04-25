package ast.expr;

import ast.exceptions.InvalidIndexException;
import ast.values.Indexed;

public class SubscriptingExpr extends Expr {
    private Expr x;
    private Expr index;

    public SubscriptingExpr(Expr x, Expr index) {
        try {
            this.type = ((Indexed) x.getType().getDefaultValue()).valueAt(0).getType();
        } catch (InvalidIndexException ignore) {}
        this.x = x;
        this.index = index;
    }

    @Override
    public String toString() {
        return "(" + x + ")[" + index + "]";
    }
}

package ast.expr;

public class SubscriptingExpr extends Expr {
    private Expr array;
    private Expr index;

    public SubscriptingExpr(Expr array, Expr index) {
        this.array = array;
        this.index = index;
    }
}

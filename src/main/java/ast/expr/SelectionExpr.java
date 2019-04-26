package ast.expr;

public class SelectionExpr extends Expr {
    private Expr expr;
    private String selection;

    public SelectionExpr(Expr expr, String selection) {
        isLValue = expr.isLValue;
        this.expr = expr;
        this.selection = selection;
    }
}

package ast.expr;

public class SelectionExpr extends Expr {
    private Expr expr;
    private String selection;

    public SelectionExpr(Expr expr, String selection) {
        this.expr = expr;
        this.selection = selection;
    }
}

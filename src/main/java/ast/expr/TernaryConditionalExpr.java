package ast.expr;

public class TernaryConditionalExpr extends Expr {
    private Expr judgement;
    private Expr x, y;

    public TernaryConditionalExpr(Expr judgement, Expr x, Expr y) {
        this.judgement = judgement;
        this.x = x;
        this.y = y;
    }
}

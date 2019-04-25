package ast.expr;

public class ReferenceExpr extends Expr {
    private String id;

    public ReferenceExpr(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}

package ast.expr;

import ast.types.Type;

public class ConstructionExpr extends Expr {
    private Expr[] exprs;

    public ConstructionExpr(Type type, Expr[] exprs) {
        this.type = type;
        this.exprs = exprs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(type.toString());
        sb.append("(");
        for (int i = 0; i < exprs.length; i++) {
            sb.append(exprs[i]);
            if (i <= exprs.length - 2) sb.append(", ");
        }
        sb.append(")");
        return new String(sb);
    }
}

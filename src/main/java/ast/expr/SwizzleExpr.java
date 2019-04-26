package ast.expr;

import ast.values.SwizzleUtility;
import ast.types.*;

public class SwizzleExpr extends Expr {
    private Expr expr;
    private int[] indices;

    public SwizzleExpr(Expr expr, int[] indices) {
        this.isLValue = expr.isLValue && !SwizzleUtility.isDuplicate(indices);
        this.expr = expr;
        this.indices = indices;
    }
}

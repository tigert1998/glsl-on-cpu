package ast.expr;

import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class SwizzleExpr extends Expr {
    private Expr expr;
    private int[] indices;

    private SwizzleExpr(Expr expr, int[] indices) {
        this.isLValue = expr.isLValue && !SwizzleUtility.isDuplicate(indices);
        this.expr = expr;
        this.indices = indices;
    }

    public static Expr factory(Expr expr, int[] indices) {
        if (expr instanceof ConstExpr) {
            var values = ((Vectorized) ((ConstExpr) expr).getValue()).retrieve();
            if (indices.length == 1) {
                try {
                    return new ConstExpr(Value.constructor(expr.getType().collapse(), new Value[]{values[indices[0]]}));
                } catch (ConstructionFailedException ignore) {
                    return null;
                }
            }
            var newValues = new Value[indices.length];
            for (int i = 0; i < indices.length; i++) newValues[i] = values[indices[i]];
            try {
                return new ConstExpr(
                        Value.constructor((Type) ((SwizzleType) expr.getType()).changeN(indices.length), newValues));
            } catch (ConstructionFailedException ignore) {
                return null;
            }
        } else {
            return new SwizzleExpr(expr, indices);
        }
    }
}

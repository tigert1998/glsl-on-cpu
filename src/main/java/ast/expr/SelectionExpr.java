package ast.expr;

import ast.exceptions.*;
import ast.values.*;

public class SelectionExpr extends Expr {
    private Expr expr;
    private String selection;

    private SelectionExpr(Expr expr, String selection) {
        isLValue = expr.isLValue;
        this.expr = expr;
        this.selection = selection;
    }

    public static Expr factory(Expr expr, String selection) throws InvalidSelectionException {
        if (expr instanceof ConstExpr) {
            StructValue value = (StructValue) ((ConstExpr) expr).getValue();
            return new ConstExpr(value.select(selection));
        } else {
            return new SelectionExpr(expr, selection);
        }
    }
}

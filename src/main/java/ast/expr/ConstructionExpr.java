package ast.expr;

import ast.types.*;
import ast.values.*;
import ast.exceptions.*;

public class ConstructionExpr extends Expr {
    private Expr[] exprs;

    private ConstructionExpr(Type type, Expr[] exprs) {
        this.type = type;
        this.exprs = exprs;
        this.isLValue = false;
    }

    public static Expr factory(Type type, Expr[] exprs) {
        var values = new Value[exprs.length];
        for (int i = 0; i < exprs.length; i++) {
            var expr = exprs[i];
            if (!(expr instanceof ConstExpr)) return new ConstructionExpr(type, exprs);
            values[i] = ((ConstExpr) expr).getValue();
        }
        try {
            return new ConstExpr(Value.constructor(type, values));
        } catch (ConstructionFailedException ignore) {
            return null;
        }
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

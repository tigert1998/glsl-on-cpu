package ast.expr;

import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.json.JSONObject;

public class SwizzleExpr extends Expr {
    private Expr expr;
    private int[] indices;

    private SwizzleExpr(Expr expr, int[] indices) {
        this.isLValue = expr.isLValue && !SwizzleUtility.isDuplicate(indices);
        this.expr = expr;
        this.indices = indices;
        var originType = (SwizzledType) expr.getType();
        if (indices.length == 1) this.type = originType.elementType();
        else this.type = (Type) originType.changeN(indices.length);
    }

    public static Expr factory(Expr expr, int[] indices)
            throws ConstructionFailedException, InvalidSelectionException {
        if (!(expr.getType() instanceof SwizzledType))
            throw InvalidSelectionException.invalidSwizzleType(expr.getType());
        var type = (SwizzledType) expr.getType();

        if (expr instanceof ConstExpr) {
            var values = ((Vectorized) ((ConstExpr) expr).getValue()).retrieve();
            if (indices.length == 1) {
                return new ConstExpr(Value.constructor(type.elementType(),
                        new Value[]{values[indices[0]]}));
            }
            var newValues = new Value[indices.length];
            for (int i = 0; i < indices.length; i++) newValues[i] = values[indices[i]];
            return new ConstExpr(
                    Value.constructor((Type) type.changeN(indices.length), newValues));

        } else if (expr instanceof SwizzleExpr) {
            int[] preIndices = ((SwizzleExpr) expr).indices;
            int[] newIndices = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                newIndices[i] = preIndices[indices[i]];
            }
            return new SwizzleExpr(((SwizzleExpr) expr).expr, newIndices);
        } else {
            return new SwizzleExpr(expr, indices);
        }
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        json.put("swizzle", SwizzleUtility.indicesToString(indices));
        return json;
    }
}

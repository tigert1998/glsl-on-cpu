package ast.expr;

import ast.exceptions.*;
import ast.operators.*;
import org.json.JSONObject;

public class AssignmentExpr extends Expr {
    private Expr x, y;

    // x = y
    public AssignmentExpr(Expr x, Expr y) throws UnlocatedSyntaxErrorException {
        if (!x.isLValue())
            throw UnlocatedSyntaxErrorException.lvalueRequired();
        if (x.getType() != y.getType())
            throw UnlocatedSyntaxErrorException.cannotConvert(y.getType(), x.getType());
        this.isLValue = true;
        this.type = x.getType();
        this.x = x;
        this.y = y;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("x", x.toJSON());
        json.put("y", y.toJSON());
        return json;
    }
}

package ast.expr;

import ast.exceptions.*;
import ast.operators.*;
import org.json.JSONObject;

public class AssignmentExpr extends Expr {
    private Expr x, y;
    BinaryOperator auxOp;

    // lvalue op y
    public AssignmentExpr(BinaryOperator auxOp, Expr x, Expr y) throws UnlocatedSyntaxErrorException {
        if (!x.isLValue())
            throw UnlocatedSyntaxErrorException.lvalueRequired();
        this.isLValue = true;
        if (auxOp != null) {
            this.type = auxOp.apply(x.getType(), y.getType());
        } else {
            if (x.getType() != y.getType())
                throw UnlocatedSyntaxErrorException.cannotConvert(y.getType(), x.getType());
            this.type = x.getType();
        }
        this.x = x;
        this.auxOp = auxOp;
        this.y = y;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("auxOp", auxOp);
        json.put("x", x.toJSON());
        json.put("y", y.toJSON());
        return json;
    }
}

package ast.expr;

import ast.stmt.DeclarationStmt;
import org.json.JSONObject;

public class ReferenceExpr extends Expr {
    private DeclarationStmt declarationStmt;

    public ReferenceExpr(DeclarationStmt declarationStmt) {
        this.declarationStmt = declarationStmt;
        isLValue = true;
        this.type = declarationStmt.type;
    }

    @Override
    protected JSONObject toJSON() {
        var json = super.toJSON();
        json.put("id", declarationStmt.id);
        return json;
    }
}

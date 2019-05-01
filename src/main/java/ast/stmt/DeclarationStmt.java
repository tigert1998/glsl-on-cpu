package ast.stmt;

import ast.*;
import ast.expr.*;
import ast.types.*;
import org.json.JSONObject;

public class DeclarationStmt extends Stmt {
    public Type type;
    public String id;
    public Expr expr;

    public DeclarationStmt(Type type, String id, Expr expr) {
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("type", type.toString());
        json.put("id", id);
        json.put("expr", ((AST) expr).toJSON());
        return json;
    }
}

package ast;

import ast.stmt.*;
import org.json.JSONObject;

public class FunctionAST extends AST {
    private FunctionSignature functionSignature;
    private CompoundStmt stmt;

    public FunctionAST(FunctionSignature functionSignature, CompoundStmt stmt) {
        this.functionSignature = functionSignature;
        this.stmt = stmt;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("signature", functionSignature.toString());
        json.put("compoundStmt", stmt.toJSON());
        return json;
    }
}

package ast;

import ast.stmt.DeclarationStmt;
import org.json.*;

import java.util.*;

public class ProgramAST extends AST {
    private List<AST> components = new ArrayList<>();

    public void putDeclarationStmt(DeclarationStmt stmt) {
        components.add(stmt);
    }

    public void putFunctionAST(FunctionAST ast) { components.add(ast); }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        components.forEach(component -> array.put(component.toJSON()));
        json.put("components", array);
        return json;
    }
}

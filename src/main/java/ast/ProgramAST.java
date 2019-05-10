package ast;

import ast.stmt.DeclarationStmt;
import org.json.*;

import java.util.*;

public class ProgramAST extends AST {
    private List<DeclarationStmt> declarationStmts = new ArrayList<>();
    private List<FunctionAST> functions = new ArrayList<>();

    public void putDeclarationStmt(DeclarationStmt stmt) {
        declarationStmts.add(stmt);
    }

    public void putFunctionAST(FunctionAST ast) { functions.add(ast); }

    public List<DeclarationStmt> getDeclarationStmts() {
        return declarationStmts;
    }

    public List<FunctionAST> getFunctions() {
        return functions;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var funcArray = new JSONArray();
        functions.forEach(function -> funcArray.put(function.toJSON()));
        json.put("functions", funcArray);
        var declArray = new JSONArray();
        declarationStmts.forEach(decl -> declArray.put(decl.toJSON()));
        json.put("declarationStmts", declArray);
        return json;
    }
}

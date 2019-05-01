package ast.stmt;

import org.json.*;

import java.util.*;

public class CompoundStmt extends Stmt {
    public List<Stmt> stmts = new ArrayList<>();

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for (var stmt : stmts) array.put(stmt.toJSON());
        json.put("stmts", array);
        return json;
    }
}

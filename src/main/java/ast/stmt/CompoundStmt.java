package ast.stmt;

import org.json.*;

import java.util.*;

public class CompoundStmt extends Stmt {
    public List<Stmt> stmts = new ArrayList<>();

    public CompoundStmt() {}

    public CompoundStmt(StmtsWrapper wrapper) {
        if (wrapper.stmts.size() == 1 && wrapper.stmts.get(0) instanceof CompoundStmt) {
            this.stmts = ((CompoundStmt) wrapper.stmts.get(0)).stmts;
        } else {
            stmts.addAll(wrapper.stmts);
        }
    }

    public static CompoundStmt singleton(Stmt stmt) {
        var compoundStmt = new CompoundStmt();
        compoundStmt.stmts.add(stmt);
        return compoundStmt;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for (var stmt : stmts) array.put(stmt.toJSON());
        json.put("stmts", array);
        return json;
    }
}

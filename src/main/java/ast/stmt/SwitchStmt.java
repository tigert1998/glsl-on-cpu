package ast.stmt;

import ast.Scope;
import ast.expr.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.*;

import java.util.*;

public class SwitchStmt extends Stmt {
    static public class CaseItem {
        public Expr expr;
        public CompoundStmt stmt;

        public CaseItem(Expr expr, CompoundStmt stmt) {
            this.expr = expr;
            this.stmt = stmt;
        }
    }

    public Expr expr;
    public List<CaseItem> caseItems = new ArrayList<>();
    private ControlFlowManager controlFlowManager;

    public SwitchStmt(Expr expr, ControlFlowManager controlFlowManager) {
        this.expr = expr;
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        // fixme
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        var array = new JSONArray();
        for (var item : caseItems) {
            var obj = new JSONObject();
            obj.put("expr", item.expr == null ? "default" : item.expr.toJSON());
            obj.put("stmt", item.stmt.toJSON());
            array.put(obj);
        }
        json.put("cases", array);
        return json;
    }
}

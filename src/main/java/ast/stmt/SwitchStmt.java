package ast.stmt;

import ast.Scope;
import ast.expr.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.*;

import java.util.*;

public class SwitchStmt extends Stmt {
    static public class CaseItem {
        public Long key;
        public CompoundStmt stmt;

        public CaseItem(Long key, CompoundStmt stmt) {
            this.key = key;
            this.stmt = stmt;
        }
    }

    private boolean containsDefault = false;
    private Set<Long> keys = new TreeSet<>();

    public Expr expr;
    private List<CaseItem> caseItems = new ArrayList<>();
    private ControlFlowManager controlFlowManager;

    public boolean addCaseItem(Long key, CompoundStmt compoundStmt) {
        if (key == null) {
            if (containsDefault) return false;
            containsDefault = true;
        } else {
            if (keys.contains(key)) return false;
            keys.add(key);
        }
        caseItems.add(new CaseItem(key, compoundStmt));
        return true;
    }

    public SwitchStmt(Expr expr, ControlFlowManager controlFlowManager) {
        this.expr = expr;
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        // todo
        var sortedCaseItems = caseItems;
        sortedCaseItems.sort((x, y) -> {
            if (x.key == null) return 1;
            if (y.key == null) return -1;
            return (int) Math.signum(x.key - y.key);
        });
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("key", expr.toJSON());
        var array = new JSONArray();
        for (var item : caseItems) {
            var obj = new JSONObject();
            obj.put("key", item.key == null ? "default" : item.key);
            obj.put("stmt", item.stmt.toJSON());
            array.put(obj);
        }
        json.put("cases", array);
        return json;
    }
}

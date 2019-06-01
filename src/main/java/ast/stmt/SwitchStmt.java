package ast.stmt;

import ast.Scope;
import ast.expr.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class SwitchStmt extends Stmt {
    static public class CaseItem {
        public Long key;
        public CompoundStmt stmt;
        LLVMBasicBlockRef block = null;

        public CaseItem(Long key, CompoundStmt stmt) {
            this.key = key;
            this.stmt = stmt;
        }
    }

    public Expr expr;

    private Set<Long> keys = new TreeSet<>();
    private List<CaseItem> caseItems = new ArrayList<>();
    private CaseItem defaultItem = null;

    private ControlFlowManager controlFlowManager;

    public boolean addCaseItem(Long key, CompoundStmt compoundStmt) {
        if (key == null) {
            // default item will appear in both `caseItems` and `defaultItem`
            if (defaultItem != null) return false;
            defaultItem = new CaseItem(null, compoundStmt);
            caseItems.add(defaultItem);
        } else {
            if (keys.contains(key)) return false;
            keys.add(key);
            caseItems.add(new CaseItem(key, compoundStmt));
        }
        return true;
    }

    public SwitchStmt(Expr expr, ControlFlowManager controlFlowManager) {
        this.expr = expr;
        this.controlFlowManager = controlFlowManager;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var value = expr.evaluate(module, function, scope);

        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        value = LLVMBuildLoad(builder, value, "");

        var caseBBMap = new TreeMap<Long, LLVMBasicBlockRef>();
        LLVMBasicBlockRef defaultBB = null;

        for (var caseItem : caseItems) {
            if (caseItem.key == null) {
                defaultBB = caseItem.block = LLVMAppendBasicBlock(function, "default");
            } else {
                caseItem.block = LLVMAppendBasicBlock(function, "case_" + caseItem.key);
                caseBBMap.put(caseItem.key, caseItem.block);
            }
            caseItem.stmt.evaluate(module, function, scope);
        }
        var end = LLVMAppendBasicBlock(function, "switch_end");

        LLVMValueRef switchHandle;
        if (defaultBB == null) {
            switchHandle = LLVMBuildSwitch(builder, value, end, caseBBMap.size());
        } else {
            switchHandle = LLVMBuildSwitch(builder, value, defaultBB, caseBBMap.size());
        }
        for (var kv : caseBBMap.entrySet()) {
            LLVMAddCase(switchHandle, constant(kv.getKey()), kv.getValue());
        }

        for (int i = 0; i < caseItems.size(); i++) {
            var caseItem = caseItems.get(i);
            LLVMPositionBuilderAtEnd(builder, caseItem.block);
            if (i == caseItems.size() - 1) {
                LLVMBuildBr(builder, end);
            } else {
                LLVMBuildBr(builder, caseItems.get(i + 1).block);
            }
        }

        controlFlowManager.evaluate(null, end);
        return null;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
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
